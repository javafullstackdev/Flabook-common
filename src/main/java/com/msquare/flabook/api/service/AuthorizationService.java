package com.msquare.flabook.api.service;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.api.exceptions.AuthenticationException;
import com.msquare.flabook.api.exceptions.AuthorizationException;
import com.msquare.flabook.common.configurations.FlabookPasswordEncoder;
import com.msquare.flabook.common.controllers.AuthorizationMessages;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.UserDto;
import com.msquare.flabook.dto.UserWithSecurityDto;
import com.msquare.flabook.dto.swagger.response.UserInfoAndUserTokenDto;
import com.msquare.flabook.enumeration.UserProfileProviderType;
import com.msquare.flabook.enumeration.UserRole;
import com.msquare.flabook.enumeration.UserStatus;
import com.msquare.flabook.exception.CommonException;
import com.msquare.flabook.form.auth.ConfirmByPhoneVo;
import com.msquare.flabook.form.auth.LoginByPhoneVo;
import com.msquare.flabook.models.User;
import com.msquare.flabook.models.UserModifyPasswordHistory;
import com.msquare.flabook.models.UserModifyTokenHistory;
import com.msquare.flabook.models.UserSecurity;
import com.msquare.flabook.repository.UserModifyTokenHistoryRepository;
import com.msquare.flabook.repository.UserRepository;
import com.msquare.flabook.repository.UserSecurityRepository;
import com.msquare.flabook.service.elasticsearch.CacheValues;
import com.msquare.flabook.util.CommonUtils;
import com.msquare.flabook.util.SendAuthMtMessage;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings({"java:S116", "unused"})
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {
	private final SendAuthMtMessage sendAuthMtMessage; //문자 메세지 발송, 지우지 마세요.

	@NonNull
	private final UserSecurityRepository userSecurityRepository;
	@NonNull
    private final UserRepository userRepository;

	@NonNull
	private final CacheManager cacheManager;
	@NonNull
	private final UserModifyTokenHistoryRepository userModifyTokenHistoryRepository;
	@NonNull
    private final PasswordEncoder passwordEncoder;

    @Value("${infobank.token.accessToken.expiredAt.minutes:1440}")
	private int ACCESS_TOKEN_EXPIRE_MINUTES;
    @Value("${infobank.token.refreshToken.expiredAt.months:1}")
	private int REFRESH_TOKEN_EXPIRE_MONTHS;

    //JoinUser 유저생성
    @Transactional(rollbackFor = Exception.class)
    public User createJoinUser(String providerId, String userName, String password, String provider) {
    	Optional<User> optionalUser = userRepository.findByProviderIdAndProvider(providerId, provider);

    	User joinUser = new User();

    	if(optionalUser.isPresent()) {
    		User user = optionalUser.get();
    		if(BooleanUtils.isTrue(user.getSecurity().getAuthStatus())) {	//사용자가 등록되어 있으며 인증을 받은 경우, 이메일 중복 처리
    			throw new CommonException(CommonResponseCode.USER_EMAIL_DUP, null);
        	}else {		//사용자가 등록되어 있으나 인증을 받지 않은 경우, 기존 user에 update
        		joinUser = optionalUser.get();
        	}
    	}

    	UserSecurity joinUserSecurity = new UserSecurity();

    	//이메일 가입 사용자인 경우
    	if(UserProfileProviderType.email.name().equals(provider)){
    		joinUserSecurity.setPassword(passwordEncoder.encode(password));
    		joinUserSecurity.setAuthStatus(false); //인증번호 입력 확인
    		joinUserSecurity.setEmail(providerId);
    	}

    	joinUserSecurity = updateUserAccessToken(joinUserSecurity, provider);
    	joinUserSecurity = updateUserRefreshToken(joinUserSecurity, provider);

    	joinUser.setProviderId(providerId);
    	joinUser.setNickname(userName);
    	joinUser.setSecurity(joinUserSecurity);
    	joinUser.setProvider(provider);
    	joinUser.setRole(UserRole.USER);
        joinUser.setStatus(UserStatus.NORMAL);

    	return joinUser;
    }

    //SNS 또는 이메일 가입
    @Transactional(rollbackFor = Exception.class)
    public UserInfoAndUserTokenDto joinUser(String providerId, String userName, String password, String provider) {
    	User joinUser = createJoinUser(providerId, userName, password, provider);

    	User joinedUser = userRepository.save(joinUser);

    	createTokenHistory(joinedUser, "join");

    	if(BooleanUtils.isTrue(joinedUser.getSecurity().getAuthStatus())) {
    		return UserInfoAndUserTokenDto.of(UserWithSecurityDto.of(joinedUser), joinedUser.getSecurity().getAccessToken(), joinedUser.getSecurity().getAccessTokenExpireAt(), joinedUser.getSecurity().getRefreshToken(), joinedUser.getSecurity().getRefreshTokenExpireAt(), joinedUser.getUserSetting().getAdNotiAgreement(), joinedUser.getUserSetting().getAdNotiConfirmedAt());
        }
        return UserInfoAndUserTokenDto.of(UserWithSecurityDto.of(joinedUser), null, null, null, null, null, null);
    }

    //사용자 AccessToken 업데이트
    public UserSecurity updateUserAccessToken(UserSecurity userSecurity, String provider) {
    	userSecurity.setAccessToken(CommonUtils.createUniqueToken());
    	if(UserProfileProviderType.phone.name().equals(provider)){
    		userSecurity.setAccessTokenExpireAt(ZonedDateTime.now().plusYears(99)); //폰, 이메일 사용자는 토큰 만료시키지 않음
    	}else {
    		userSecurity.setAccessTokenExpireAt(ZonedDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRE_MINUTES));
    	}

    	return userSecurity;
    }

    //사용자 refreshToken 업데이트
    public UserSecurity updateUserRefreshToken(UserSecurity userSecurity, String provider) {
    	userSecurity.setRefreshToken(CommonUtils.createUniqueToken());
    	if(UserProfileProviderType.phone.name().equals(provider)){
    		userSecurity.setRefreshTokenExpireAt(ZonedDateTime.now().plusYears(99)); //폰, 이메일 사용자는 토큰 만료시키지 않음
    	}else {
    		userSecurity.setRefreshTokenExpireAt(ZonedDateTime.now().plusMonths(REFRESH_TOKEN_EXPIRE_MONTHS));
    	}

    	return userSecurity;
    }

    //이메일 로그인
    @Transactional(rollbackFor = Exception.class)
    public UserInfoAndUserTokenDto loginUser(String providerId, String password, String provider) {

    	log.info("loginUser - [{}][{}]", providerId, provider);

    	User user = userRepository.findByProviderIdAndProvider(providerId, provider).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));

    	//이메일 가입 사용자 && 비밀번호 미일치
    	if ("email".equals(provider) && !passwordEncoder.matches(password, user.getSecurity().getPassword())) {
    		throw new CommonException(CommonResponseCode.USER_PASSWORD_MISS_MATCH, null);
    	}

    	//인증번호 미입력 사용자
    	if(BooleanUtils.isFalse(user.getSecurity().getAuthStatus())) {
			UserInfoAndUserTokenDto dto = UserInfoAndUserTokenDto.of(UserDto.of(user), null, null, null, null, null, null);
    		throw new CommonException(CommonResponseCode.USER_AUTHKEY_CONFIRM_NEED, dto);
    	}

    	//로그인 시 token 재발급
		return updateUserToken(user);
    }


    //핸드폰번호 로그인
    @Transactional(rollbackFor = Exception.class)
    public UserInfoAndUserTokenDto loginUserByPhone(LoginByPhoneVo loginUser) {

    	log.info("loginUserByPhone - [{}][{}]", loginUser.getAuthId(), "phone");

    	/*기존 모야모 로그인과 동일하게*/
    	User user = userRepository.findByProviderIdAndProvider(loginUser.getAuthId(), UserProfileProviderType.phone.name()).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));
    	UserSecurity userSecurity = user.getSecurity();

    	//핸드폰번호 가입 사용자 && 닉네임 미일치
    	//TODO : 마이그레이션하면 기존 사용자 닉네임 변경이 생기거나 사용자가 직접 입력하기 힘든 닉네임이 다수 존재하므로 기획 확인이 필요
    	//현재 사용하지 않음, 핸드폰 중복으로 인하여 닉네임도 같이 비교
		/*if (!loginUser.getNickName().equals(user.getNickname())) {
			throw new CommonException(CommonResponseCode.USER_NAME_LOGIN_FAIL, null);
		}*/

    	//인증번호 발송 내역이 존재하지 않습니다.
        if(userSecurity.getAuthIdKeySendedAt() == null) {
        	throw new CommonException(CommonResponseCode.FAIL_AUTH_HISTORY_NOT_FOUND, null);
        }

		//핸드폰번호로 인증번호 발송 이력이 있으며 발송 후 AUTH_ID_KEY_CONFIRM_AT_PLUS_MINUTES 인증번호 확인 시간이 지난 사용자
		if(userSecurity.getAuthIdKeySendedAt() != null && ZonedDateTime.now().isAfter(userSecurity.getAuthIdKeySendedAt().plusMinutes(AuthorizationMessages.AUTH_ID_KEY_CONFIRM_AT_PLUS_MINUTES))){
			log.info("인증번호 확인 " + AuthorizationMessages.AUTH_ID_KEY_CONFIRM_AT_PLUS_MINUTES + "분 제한시간 초과");
			throw new CommonException(CommonResponseCode.USER_AUTHKEY_LATE_FAIL, null);
		}

		//핸드폰번호 가입 사용자 && 인증번호 미일치
		if (user.getSecurity().getAuthIdKey() != null && !loginUser.getAuthIdKey().equals(user.getSecurity().getAuthIdKey())) {
			throw new CommonException(CommonResponseCode.USER_AUTHKEY_MISS_MATCH, null);
		}

		//기존 모야모 패스워드 확인
		String password = FlabookPasswordEncoder.hashedPassword(loginUser.getNickName(), user.getSecurity().getSalt());
		if(!user.getSecurity().getPassword().equals(password)) {
			throw new CommonException(CommonResponseCode.USER_NAME_LOGIN_FAIL, null);
		}

    	//로그인 시 token 재발급
		return updateUserToken(user);
    }

	/**
	 * 사용자 토큰 생성 및 토큰 발급 결과
	 * @param user User
	 * @return UserInfoAndUserTokenDto 토큰 발급 결과
	 */
	private UserInfoAndUserTokenDto updateUserToken(User user) {
		updateUserAccessToken(user.getSecurity(), user.getProvider());
		updateUserRefreshToken(user.getSecurity(), user.getProvider());
		createTokenHistory(user, "login");
		userRepository.save(user);
		return UserInfoAndUserTokenDto.of(UserWithSecurityDto.of(user), user.getSecurity().getAccessToken(), user.getSecurity().getAccessTokenExpireAt(), user.getSecurity().getRefreshToken(), user.getSecurity().getRefreshTokenExpireAt(), user.getUserSetting().getAdNotiAgreement(), user.getUserSetting().getAdNotiConfirmedAt());
	}


	/*핸드폰번호 로그인 인증 확인 문자 발송*/
    public Boolean sendAuthIdKey(ConfirmByPhoneVo confirmUser) throws Exception {

		User user = userRepository.findByProviderIdAndProvider(confirmUser.getAuthId(), UserProfileProviderType.phone.name()).orElse(null);

		if(user == null) {
			//sns 전환계정 확인. 전환된 계정 정보 전달
			List<Tuple> histories = userRepository.findModifiedUserByProviderId(confirmUser.getAuthId());
			if (!histories.isEmpty()) {

				Tuple tuple = histories.get(0);
				Long userId = (Long)tuple.get(1);
				//상용에서 UserModifyProviderHistory.User lazy 로딩이 안돼서 사용자 id로 조회
				user = userRepository.findById(userId).orElseThrow(() -> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));

				UserProfileProviderType providerType = UserProfileProviderType.valueOf(user.getProvider());
				throw new CommonException(CommonResponseCode.USER_SNS_PROVIDER_CHANGE.getResultCode(),  CommonResponseCode.USER_SNS_PROVIDER_CHANGE.getResultMessage().replace("#PROVIDER_DISPLAY_NAME#", providerType.getDisplayName()));

			} else {
				throw new CommonException(CommonResponseCode.USER_NOT_EXIST, null);
			}
		}

    	UserSecurity userSecurity = user.getSecurity();

    	//인증번호 확인 문자 발송 이력이 있으며 발송 후 AUTH_ID_KEY_SEND_AT_PLUS_MINUTES(인증문자 재발송 시간)분이 지나지 않은 사용자
    	if(userSecurity.getAuthIdKeySendedAt() != null && ZonedDateTime.now().isBefore(userSecurity.getAuthIdKeySendedAt().plusMinutes(AuthorizationMessages.AUTH_ID_KEY_SEND_AT_PLUS_MINUTES))){
    		log.info("인증번호 발송 " + AuthorizationMessages.AUTH_ID_KEY_SEND_AT_PLUS_MINUTES + "분내 재시도");

    		//재발송 남은 시간 구하기
    		long authLimitTime = CommonUtils.getAuthRemainingTime(AuthorizationMessages.AUTH_ID_KEY_SEND_AT_PLUS_MINUTES, userSecurity.getAuthIdKeySendedAt(), ZonedDateTime.now());

    		throw new CommonException(CommonResponseCode.USER_AUTHKEY_ALREADY_SEND_PHONE.getResultCode(), CommonResponseCode.USER_AUTHKEY_ALREADY_SEND_PHONE.getResultMessage().replace("#RESEND_TIME#", String.valueOf(authLimitTime)), null);
    	}

    	String password = FlabookPasswordEncoder.hashedPassword(confirmUser.getNickName(), user.getSecurity().getSalt());

    	//계정전환된 계정이거나 비밀번호가 맞으면 인증번호를 발송하고 인증번호가 인증되면 전환된계정 알림
    	if(!user.getSecurity().getPassword().equals(password)) {
    		throw new CommonException(CommonResponseCode.USER_NAME_LOGIN_FAIL, null);
    	}

    	int getAuthIdKey = CommonUtils.createRandomNumber(6);

    	String authIdKey = BooleanUtils.isTrue(sendAuthMtMessage.isTestMode()) ? "111111" : String.valueOf(getAuthIdKey);
    	userSecurity.setAuthIdKey(authIdKey);
    	userSecurity.setAuthIdKeySendedAt(ZonedDateTime.now());
    	userRepository.save(user);

    	//설정에 따라 발송되도록 수정
        return sendAuthMtMessage.send(confirmUser.getAuthId(), String.valueOf(getAuthIdKey));
    }

	/*이름 중복 확인*/
    public Boolean confirmUserByUserName(String userName, UserStatus status) {

    	List<User> users = userRepository.findByNicknameAndStatusNotAndSecurityAuthStatus(userName, status, true);
    	for(User user : users) {
			if(BooleanUtils.isTrue(user.getSecurity().getAuthStatus())) {	//사용자가 등록되어 있으며 인증을 받은 경우, 닉네임 중복 처리
				throw new CommonException(CommonResponseCode.USER_NAME_DUP, null);
			}
		}
        return false;
    }

    /*이메일 중복 확인*/
    public Boolean confirmUserByEmail(String providerId) {

    	Optional<User> user = userRepository.findByProviderIdAndProvider(providerId, UserProfileProviderType.email.name());

    	if(user.isPresent() && BooleanUtils.isTrue(user.get().getSecurity().getAuthStatus())) {
			//사용자가 등록되어 있으며 인증을 받은 경우, 이메일 중복 처리
			throw new CommonException(CommonResponseCode.USER_EMAIL_DUP, null);
    	}

        return false;
    }

	/*이메일 가입, 이메일 인증번호 발송 정보 생성*/
    @Transactional(rollbackFor = Exception.class)
    public User getAuthorizationMail(Long userInfoId) {

    	User user = userRepository.findById(userInfoId).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));
    	UserSecurity userSecurity = user.getSecurity();

    	//이미 인증번호 입력한 사용자
    	if(BooleanUtils.isTrue(user.getSecurity().getAuthStatus())) {
        	throw new CommonException(CommonResponseCode.USER_AUTHKEY_ALREADY_MATCH, null);
        }

    	//인증번호 확인 메일 발송 이력이 있으며 발송 후 AUTH_MAIL_SEND_AT_PLUS_MINUTES(인증메일 재발송 시간)분이 지나지 않은 사용자
    	if(userSecurity.getAuthMailSendedAt() != null && ZonedDateTime.now().isBefore(userSecurity.getAuthMailSendedAt().plusMinutes(AuthorizationMessages.AUTH_MAIL_SEND_AT_PLUS_MINUTES))){
    		log.info("인증메일 발송 " + AuthorizationMessages.AUTH_MAIL_SEND_AT_PLUS_MINUTES + "분내 재시도");

    		//재발송 남은 시간 구하기
    		long authLimitTime = CommonUtils. getAuthRemainingTime(AuthorizationMessages.AUTH_MAIL_SEND_AT_PLUS_MINUTES, userSecurity.getAuthMailSendedAt(), ZonedDateTime.now());

    		throw new CommonException(CommonResponseCode.USER_AUTHKEY_ALREADY_SEND_MAIL.getResultCode(), CommonResponseCode.USER_AUTHKEY_ALREADY_SEND_MAIL.getResultMessage().replace("#RESEND_TIME#", String.valueOf(authLimitTime)), null);
    	}

    	String authKey = String.valueOf(CommonUtils.createRandomNumber(6));

    	userSecurity.setAuthKey(authKey);
    	userSecurity.setAuthMailSendedAt(ZonedDateTime.now());
    	userRepository.save(user);

        return user;
    }

	/*이메일 가입, 인증번호 확인*/
    @Transactional(rollbackFor = Exception.class)
    public UserInfoAndUserTokenDto confirmAuthKey(Long userInfoId, String authKey) {

    	User user = userRepository.findById(userInfoId).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));
    	UserSecurity userSecurity = user.getSecurity();

        //이미 인증번호 입력한 사용자
        if(BooleanUtils.isTrue(userSecurity.getAuthStatus())) {
        	throw new CommonException(CommonResponseCode.USER_AUTHKEY_ALREADY_MATCH, null);
        }

        //인증번호 발송 내역이 존재하지 않습니다.
        if(userSecurity.getAuthMailSendedAt() == null) {
        	throw new CommonException(CommonResponseCode.FAIL_AUTH_HISTORY_NOT_FOUND, null);
        }

        //인증번호 확인 메일 발송 이력이 있으며 발송 후 AUTH_MAIL_CONFIRM_AT_PLUS_MINUTES(인증번호 입력 확인 시간)분이 지난 사용자
        if(userSecurity.getAuthMailSendedAt() != null && ZonedDateTime.now().isAfter(userSecurity.getAuthMailSendedAt().plusMinutes(AuthorizationMessages.AUTH_MAIL_CONFIRM_AT_PLUS_MINUTES))){
    		log.info("인증키 확인 " + AuthorizationMessages.AUTH_MAIL_CONFIRM_AT_PLUS_MINUTES + "분 제한시간 초과");

    		throw new CommonException(CommonResponseCode.USER_AUTHKEY_LATE_FAIL, null);
    	}

        //인증번호 미일치
        if(userSecurity.getAuthKey() != null && !userSecurity.getAuthKey().equals(authKey)) {
        	throw new CommonException(CommonResponseCode.USER_AUTHKEY_MISS_MATCH, null);
        }

        userSecurity.setAuthStatus(true);
        userRepository.save(user);

        return UserInfoAndUserTokenDto.of(UserWithSecurityDto.of(user), user.getSecurity().getAccessToken(), user.getSecurity().getAccessTokenExpireAt(), user.getSecurity().getRefreshToken(), user.getSecurity().getRefreshTokenExpireAt(), user.getUserSetting().getAdNotiAgreement(), user.getUserSetting().getAdNotiConfirmedAt());
    }

    @Transactional(rollbackFor = Exception.class)
    public void createTokenHistory(User user, String event) {
		UserModifyTokenHistory userModifyTokenHistory = new UserModifyTokenHistory();
		userModifyTokenHistory.setUserId(user.getId());
		userModifyTokenHistory.setAccessToken(user.getSecurity().getAccessToken());
		userModifyTokenHistory.setRefreshToken(user.getSecurity().getRefreshToken());
		userModifyTokenHistory.setEvent(event);
		userModifyTokenHistoryRepository.save(userModifyTokenHistory);
    }

    /*사용자 AccessToken 갱신*/
    @Transactional(rollbackFor = Exception.class)
    public UserInfoAndUserTokenDto updateAccessTokenByRefreshToken(String refreshToken) {
    	User user = userRepository.findBySecurityRefreshToken(refreshToken).orElseThrow(() -> new CommonException(CommonResponseCode.TOKEN_INVALID, null));

    	if(ZonedDateTime.now().isAfter(user.getSecurity().getRefreshTokenExpireAt())){
    		throw new CommonException(CommonResponseCode.REFRESHTOKEN_EXPIRE, null);
        }

    	updateUserAccessToken(user.getSecurity(), user.getProvider());
		updateUserRefreshToken(user.getSecurity(), user.getProvider());
    	User updatedUser = userRepository.save(user);

    	createTokenHistory(updatedUser, "refresh");


    	return UserInfoAndUserTokenDto.of(UserWithSecurityDto.of(user), user.getSecurity().getAccessToken(), user.getSecurity().getAccessTokenExpireAt(), user.getSecurity().getRefreshToken(), user.getSecurity().getRefreshTokenExpireAt(), user.getUserSetting().getAdNotiAgreement(), user.getUserSetting().getAdNotiConfirmedAt());
    }

	@Data
	@AllArgsConstructor
	public static class Expires implements Serializable {
		private long id;
		private long expire;

		public Long valid() throws AuthenticationException {

			if(this.expire > Timestamp.from(ZonedDateTime.now().toInstant()).getTime()) {
				return id;
			}

			throw new AuthenticationException("토큰이 만료되었습니다.");
		}

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Security {
		private Long id;
		private ZonedDateTime accessTokenExpireAt;
	}


	/**
	 * @param accessToken 인증토큰
	 * @return Expires
	 * @throws AuthenticationException 인증에러
	 */
	@Cacheable(cacheResolver = "localCacheResolver", value = CacheValues.FIND_USER_BY_ACCESS_TOKEN, key = "#accessToken")
	public Expires findUserIdByAccessToken(String accessToken) throws AuthenticationException {
		Tuple tuple = userSecurityRepository.findIdWithAccessTokenExpireAtByAccessToken(accessToken).orElseThrow(() -> new CommonException(CommonResponseCode.TOKEN_INVALID, null));

		Security security = new Security(tuple.get(0, Long.class), tuple.get(1, ZonedDateTime.class));

		if(!getTokenExpiredAuthentication(security)) {
			throw new AuthenticationException("토큰이 만료되었습니다.");
		}

		Long userId = userRepository.findIdBySecurityId(security.getId());
		return new Expires(userId, Timestamp.from(security.getAccessTokenExpireAt().toInstant()).getTime());
	}

	/**
	 * @param id 사용자Id
	 * @return UserSecurity
	 * @throws CommonException 에러
	 */
	@SuppressWarnings("UnusedReturnValue")
	@Transactional
	@CacheEvict(cacheResolver = "localCacheResolver", value = CacheValues.FIND_USER_BY_ACCESS_TOKEN, key = "#result.accessToken")
	public UserSecurity expireUserAccessToken(long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new CommonException(CommonResponseCode.UNKNOWN_RESPONSE_TYPE, null));
		user.getSecurity().setAccessTokenExpireAt(ZonedDateTime.now().minusMinutes(1000));
		return user.getSecurity();
	}


	/**
	 * @param id 사용자ID
	 * @return User
	 * @throws AuthorizationException 인증에러예외
	 */
	public User findUserById(Long id) throws AuthorizationException {
		User user = userRepository.findById(id).orElseThrow(() -> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));
		if(!getUserStatusAuthentication(user)) {
			throw new AuthorizationException("권한이 없는 사용자입니다.");
		}
		return user;
	}


	public static boolean getTokenExpiredAuthentication(UserSecurity userSecurity) {
		return !ZonedDateTime.now().isAfter(userSecurity.getAccessTokenExpireAt());
	}

	public static boolean getTokenExpiredAuthentication(Security security) {
		return !(security == null || security.getAccessTokenExpireAt() == null || ZonedDateTime.now().isAfter(security.getAccessTokenExpireAt()));
	}

	public static  boolean getUserStatusAuthentication(User user) {
		return UserStatus.NORMAL.equals(user.getStatus());

	}








	 /**
     * 사용자 비밀번호 찾기
     * 비밀번호 변경 개인화 URL 전송
     */
    @Transactional(rollbackFor = Exception.class)
    public String modifyPasswordSendMail(String providerId, String provider) {

    	User user = userRepository.findByProviderIdAndProvider(providerId, provider).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));

    	String authKey = UUID.randomUUID().toString();

    	//기존 사용자 정보 History 저장
		UserModifyPasswordHistory userModifyPasswordHistory = new UserModifyPasswordHistory();
		userModifyPasswordHistory.setAuthKey(authKey);
		user.getUserModifyPasswordHistory().add(userModifyPasswordHistory);

		userRepository.save(user);

        return authKey;
    }
    /**
     * 사용자 비밀번호 찾기
     * URL을 통해 비밀번호 변경
     */
    @SuppressWarnings("UnusedReturnValue")
    @Transactional(rollbackFor = Exception.class)
    public String modifyPassword(String password, String authKey, String provider) {

    	User user = userRepository.findByuserModifyPasswordHistoryAuthKey(authKey).orElseThrow(()-> new CommonException(CommonResponseCode.USER_SNS_GET_PROFILE_FAIL, null));
    	if(log.isDebugEnabled()) {
			log.debug("user.getNickname() : {}", user.getNickname());
			log.debug("user.password() : {}", password);
		}
    	user.getSecurity().setPassword(passwordEncoder.encode(password));
    	user.getUserModifyPasswordHistory().get(user.getUserModifyPasswordHistory().size()-1).setAuthStatus(true);
    	userRepository.save(user);

    	return authKey;
    }

    /*
     * 사용자 비밀번호 찾기
     * URL 유효성 체크
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean modifyPasswordUrlCheck(String authKey) {

    	User user = userRepository.findByuserModifyPasswordHistoryAuthKey(authKey).orElseThrow(()-> new CommonException(CommonResponseCode.USER_SNS_GET_PROFILE_FAIL, false));

    	UserModifyPasswordHistory history = user.getUserModifyPasswordHistory().get(user.getUserModifyPasswordHistory().size()-1);

    	if(!authKey.equals(history.getAuthKey())) {
    		log.info("!authKey.equals(history.getAuthKey() : 유효하지 않은 URL 입니다.");
    		throw new CommonException(CommonResponseCode.FAIL.getResultCode(), "유효하지 않은 URL입니다.", false);
    	}

    	if(BooleanUtils.isTrue(history.getAuthStatus())) { //이미 변경을 한 경우
    		log.info("history.getAuthStatus() : 유효하지 않은 URL 입니다.");
    		throw new CommonException(CommonResponseCode.FAIL.getResultCode(), "유효하지 않은 URL입니다.", false);
    	}

    	//비밀번호 변경 이메일 유효시간 체크
    	if(history.getCreatedAt() != null && ZonedDateTime.now().isAfter(history.getCreatedAt().plusMinutes(AuthorizationMessages.PASSWORD_MODIFY_MAIL_CONFIRM_AT_PLUS_MINUTES))){
    		log.info("비밀번호 변경 유효시간 " + AuthorizationMessages.PASSWORD_MODIFY_MAIL_CONFIRM_AT_PLUS_MINUTES + "분 초과");
    		throw new CommonException(CommonResponseCode.USER_AUTHKEY_LATE_FAIL.getResultCode(), "비밀번호 변경 가능 시간 초과 되었습니다.", false);
    	}

    	return true;
    }

}

