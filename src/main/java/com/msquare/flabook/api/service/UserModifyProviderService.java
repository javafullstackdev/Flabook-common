package com.msquare.flabook.api.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.AuthorizationMessages;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.ShopUserDto;
import com.msquare.flabook.dto.UserDto;
import com.msquare.flabook.dto.UserModifyProviderLoginDto;
import com.msquare.flabook.enumeration.MailType;
import com.msquare.flabook.enumeration.UserProfileProviderType;
import com.msquare.flabook.exception.CommonException;
import com.msquare.flabook.form.CreateMailVo;
import com.msquare.flabook.form.auth.MyProfileEmailVo;
import com.msquare.flabook.models.User;
import com.msquare.flabook.models.UserModifyProviderAuth;
import com.msquare.flabook.models.UserModifyProviderHistory;
import com.msquare.flabook.models.UserSecurity;
import com.msquare.flabook.repository.UserRepository;
import com.msquare.flabook.util.CommonUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserModifyProviderService {

	@NonNull
	private final UserRepository userRepository;
	@NonNull
	private final PasswordEncoder passwordEncoder;

	@Value("${infobank.token.accessToken.expiredAt.minutes:1440}")
	private int accessTokenExpireMinutes;
    @Value("${infobank.token.refreshToken.expiredAt.months:1}")
	private int refreshTokenExpireMonths;

	private static String getShopUserId(User user) {
		return (user.getShopUserId() != null && !user.getShopUserId().isEmpty()) ?  user.getShopUserId() : ShopUserDto.of(user).getShopUserId();
	}

	@SuppressWarnings("unused")
	@Cacheable(value = "user", key = "#id")
	public Optional<UserDto> findUser(long id) {
		return userRepository.findById(id).map(UserDto::of);
	}

	/**
	 * 계정연동
	 * 기존 휴대폰 로그인 사용자 -> sns 로그인으로 전환
	 *
	 * @param userInfo 사용자 정보
	 * @return boolean 연동 결과
	 */
	@Transactional(rollbackFor = Exception.class)
	public UserModifyProviderLoginDto updateUserLoginProviderBySns(User userInfo, String providerId, String nickname, @SuppressWarnings("unused") String password, UserProfileProviderType provider) {

		Optional<User> getUserInfo = userRepository.findByProviderIdAndProvider(providerId, provider.name());
		if(getUserInfo.isPresent() && BooleanUtils.isTrue(getUserInfo.get().getSecurity().getAuthStatus())) {
			//사용자가 등록되어 있으며 인증을 받은 경우, 중복 처리
			throw new CommonException(CommonResponseCode.USER_LOGIN_DUP, null);
		}

		User user = userRepository.findById(userInfo.getId()).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));

		//전화번호+id 의 쇼핑몰 id 를 가져온다.
		String oldShopUserId = getShopUserId(user);

		//휴대폰 로그인 사용자가 아닌 경우 체크
		if(!user.getProvider().equals(UserProfileProviderType.phone.name())) {
			throw new CommonException(CommonResponseCode.USER_PROVIDER_NOT_SUPPORT, UserProfileProviderType.phone.name());
		}

		//이메일 Provider 체크
		if(user.getProvider().equals(UserProfileProviderType.email.name())) {
			throw new CommonException(CommonResponseCode.USER_PROVIDER_NOT_SUPPORT, UserProfileProviderType.email.name());
		}

		//기존 사용자 정보 History 저장
		UserModifyProviderHistory userModifyProviderHistory = new UserModifyProviderHistory();
		userModifyProviderHistory.setProviderId(user.getProviderId());
		userModifyProviderHistory.setNickname(user.getNickname());
		userModifyProviderHistory.setProvider(provider.name());
		user.setUserModifyProviderHistory(userModifyProviderHistory);
		user.setShopUserId(oldShopUserId);

		//토큰 발급
		user.getSecurity().setAccessTokenExpireAt(ZonedDateTime.now().plusMinutes(accessTokenExpireMinutes));
		user.getSecurity().setRefreshToken(CommonUtils.createUniqueToken());
		user.getSecurity().setRefreshTokenExpireAt(ZonedDateTime.now().plusMonths(refreshTokenExpireMonths));

		//기존 휴대폰 로그인 사용자 -> 새로운 provider로 전환
		UserSecurity userSecurity = user.getSecurity();
		user.setProvider(provider.name());
		user.setProviderId(providerId);
		user.setNickname(nickname);
		userSecurity.setSalt(null);

		if(!user.getProvider().equals(UserProfileProviderType.email.name())) {
			userSecurity.setAuthStatus(true);
		}


		User updatedUser = userRepository.save(user);

		//기존 휴대폰 로그인 사용자 상태 변경, 로그인 불가

		return UserModifyProviderLoginDto.of(updatedUser, updatedUser.getSecurity().getAccessToken(), updatedUser.getSecurity().getAccessTokenExpireAt(), updatedUser.getSecurity().getRefreshToken(), updatedUser.getSecurity().getRefreshTokenExpireAt(), updatedUser.getUserSetting().getAdNotiAgreement(), updatedUser.getUserSetting().getAdNotiConfirmedAt());
	}

	/**
	 * 계정연동
	 * 기존 휴대폰 로그인 사용자 - email 인증 번호 발송
	 *
	 * @param userInfo 사용자 정보
	 * @return boolean 연동 결과
	 */
	@Transactional(rollbackFor = Exception.class)
	public UserDto createLoginProviderByEmail(User userInfo, String providerId, String nickname, String password, UserProfileProviderType provider) {

		User user = userRepository.findById(userInfo.getId()).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));

		//휴대폰 로그인 사용자가 아닌 경우 체크
		if(!user.getProvider().equals(UserProfileProviderType.phone.name())) {
			throw new CommonException(CommonResponseCode.USER_PROVIDER_NOT_SUPPORT, null);
		}

		Optional<User> getUserInfo = userRepository.findByProviderIdAndProvider(providerId, provider.name());
		if(getUserInfo.isPresent()) {
			if(BooleanUtils.isTrue(getUserInfo.get().getSecurity().getAuthStatus())) {	//사용자가 등록되어 있으며 인증을 받은 경우, 중복 처리
				throw new CommonException(CommonResponseCode.USER_LOGIN_DUP, null);
			}else {
				userRepository.delete(getUserInfo.get()); //임시 가입 정보 삭제
			}
		}

		user = sendEmailAuthKey(user, providerId, nickname, password);
		User updatedUser = userRepository.save(user);

		return UserDto.of(updatedUser);
	}

	/**
	 * 계정연동
	 * 기존 휴대폰 로그인 사용자 - email 인증 번호 확인 및 전환
	 *
	 * @param currentUser 사용자 정보
	 * @param vo 사용자 이메일
	 * @return boolean 인증번호 발송 결과
	 */
	@Transactional(rollbackFor = Exception.class)
	public UserModifyProviderLoginDto updateLoginProviderByEmail(User currentUser, MyProfileEmailVo vo) {

		User user = userRepository.findById(currentUser.getId()).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));

		if(!user.getUserModifyProviderAuth().isEmpty()) {

			//전화번호+id 의 쇼핑몰 id 를 가져온다.
			String oldShopUserId = getShopUserId(user);
			UserModifyProviderAuth userModifyProviderAuth = user.getUserModifyProviderAuth().get(user.getUserModifyProviderAuth().size()-1);

			//이미 인증번호 입력한 사용자
			if(BooleanUtils.isTrue(userModifyProviderAuth.getAuthStatus())) {
				throw new CommonException(CommonResponseCode.USER_AUTHKEY_ALREADY_MATCH, false);
			}

			//이메일로 인증번호 발송 이력이 있으며 발송 후 AuthorizationMessages.AUTH_MAIL_CONFIRM_AT_PLUS_MINUTES(재발송 시간)분이 지난 사용자
			if(userModifyProviderAuth.getSendedAt() != null && ZonedDateTime.now().isAfter(userModifyProviderAuth.getSendedAt().plusMinutes(AuthorizationMessages.AUTH_MAIL_CONFIRM_AT_PLUS_MINUTES))){
				log.info("인증번호 확인 " + AuthorizationMessages.AUTH_MAIL_CONFIRM_AT_PLUS_MINUTES + "분 제한시간 초과");
				throw new CommonException(CommonResponseCode.USER_AUTHKEY_LATE_FAIL, false);
			}

			//인증번호 미일치
			if(!(vo.getEmail().equals(userModifyProviderAuth.getEmail()) && vo.getAuthKey().equals(userModifyProviderAuth.getAuthKey()))) {
				throw new CommonException(CommonResponseCode.USER_AUTHKEY_MISS_MATCH, false);
			}

			//인증 완료
			userModifyProviderAuth.setAuthStatus(true);

			//사용자 로그인 provider 전환
			user.setProvider(UserProfileProviderType.email.name());
			user.setProviderId(userModifyProviderAuth.getEmail());
			user.setNickname(userModifyProviderAuth.getNickname());
			user.getSecurity().setPassword(userModifyProviderAuth.getPassword());
			user.getSecurity().setSalt(null);
			user.setShopUserId(oldShopUserId);

			//토큰 발급
			user.getSecurity().setAccessTokenExpireAt(ZonedDateTime.now().plusMinutes(accessTokenExpireMinutes));
			user.getSecurity().setRefreshToken(CommonUtils.createUniqueToken());
			user.getSecurity().setRefreshTokenExpireAt(ZonedDateTime.now().plusMonths(refreshTokenExpireMonths));

			//등록된 이메일이 없을 경우 사용자 이메일 정보 등록
			if(user.getSecurity().getEmail() == null) {
				user.getSecurity().setEmail(userModifyProviderAuth.getEmail());
			}

			//기존 사용자 정보 History 저장
			UserModifyProviderHistory userModifyProviderHistory = new UserModifyProviderHistory();
			userModifyProviderHistory.setProviderId(currentUser.getProviderId());
			userModifyProviderHistory.setNickname(currentUser.getNickname());
			userModifyProviderHistory.setProvider(UserProfileProviderType.email.name());
			user.setUserModifyProviderHistory(userModifyProviderHistory);

			User updatedUser = userRepository.save(user);
			return UserModifyProviderLoginDto.of(updatedUser, updatedUser.getSecurity().getAccessToken(), updatedUser.getSecurity().getAccessTokenExpireAt(), updatedUser.getSecurity().getRefreshToken(), updatedUser.getSecurity().getRefreshTokenExpireAt(), updatedUser.getUserSetting().getAdNotiAgreement(), updatedUser.getUserSetting().getAdNotiConfirmedAt());
		}else {
			throw new CommonException(CommonResponseCode.FAIL, false);
		}
	}

	/**
	 * 계정연동
	 * 기존 휴대폰 로그인 사용자 - email 인증 번호 발송
	 *
	 * @param user 사용자 정보
	 * @param providerId 사용자 이메일
	 * @return boolean 인증번호 발송 결과
	 */
	@Transactional(rollbackFor = Exception.class)
	public User sendEmailAuthKey(User user, String providerId, String nickname, String password) {

		if(!user.getUserModifyProviderAuth().isEmpty()) {

			UserModifyProviderAuth userModifyProviderAuth = user.getUserModifyProviderAuth().get(user.getUserModifyProviderAuth().size()-1);

			//인증번호 확인 이메일 발송 이력이 있으며 발송 후 AuthorizationMessages.AUTH_MAIL_SEND_AT_PLUS_MINUTES(인증메일 재발송 시간)분이 지나지 않은 사용자
			if(userModifyProviderAuth.getSendedAt() != null && ZonedDateTime.now().isBefore(userModifyProviderAuth.getSendedAt().plusMinutes(AuthorizationMessages.AUTH_MAIL_SEND_AT_PLUS_MINUTES))){
				log.info("인증번호 발송 " + AuthorizationMessages.AUTH_MAIL_SEND_AT_PLUS_MINUTES + "분내 재시도");

				//재발송 남은 시간 구하기
				long authLimitTime = CommonUtils.getAuthRemainingTime(AuthorizationMessages.AUTH_MAIL_SEND_AT_PLUS_MINUTES, userModifyProviderAuth.getSendedAt(), ZonedDateTime.now());

				throw new CommonException(CommonResponseCode.USER_AUTHKEY_ALREADY_SEND_PHONE.getResultCode(), CommonResponseCode.USER_AUTHKEY_ALREADY_SEND_PHONE.getResultMessage().replace("#RESEND_TIME#", String.valueOf(authLimitTime)), false);
			}
		}

		String authKey = String.valueOf(CommonUtils.createRandomNumber(6));

		//이메일 인증 정보 확인을 위한 데이터 저장
		UserModifyProviderAuth userModifyProviderAuth = new UserModifyProviderAuth();
		userModifyProviderAuth.setNickname(nickname);
		userModifyProviderAuth.setPassword(passwordEncoder.encode(password));
		userModifyProviderAuth.setEmail(providerId);
		userModifyProviderAuth.setAuthStatus(false);
		userModifyProviderAuth.setAuthKey(authKey);
		userModifyProviderAuth.setSendedAt(ZonedDateTime.now());

		user.getUserModifyProviderAuth().add(userModifyProviderAuth);

		//인증 메일 발송
		CreateMailVo createMailVo = CreateMailVo.builder()
				.nickName(userModifyProviderAuth.getNickname())
				.authKey(authKey)
				.email(userModifyProviderAuth.getEmail())
				.type(MailType.modifyProvider)
				.build();

		sendMailAsyncService.sendMail(createMailVo);

		return user;
	}

	private final SendMailAsyncService sendMailAsyncService;
}

