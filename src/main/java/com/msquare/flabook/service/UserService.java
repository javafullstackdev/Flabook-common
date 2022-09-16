package com.msquare.flabook.service;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.configurations.SubscribeTopics;
import com.msquare.flabook.common.controllers.AuthorizationMessages;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.*;
import com.msquare.flabook.enumeration.UserStatus;
import com.msquare.flabook.exception.CommonException;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.form.UpdateProfileAvatarVo;
import com.msquare.flabook.form.UserSettingVo;
import com.msquare.flabook.form.auth.*;
import com.msquare.flabook.models.*;
import com.msquare.flabook.repository.*;
import com.msquare.flabook.service.elasticsearch.CacheValues;
import com.msquare.flabook.util.CommonUtils;
import com.msquare.flabook.util.SendAuthMtMessage;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.shiro.util.CollectionUtils;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.io.IOException;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserService  extends AbstractSearchIndexer {

    private static final String NICKNAME_FIELD = "nickname";
	private final SendAuthMtMessage sendAuthMtMessage; //문자 메세지 발송, 지우지 마세요.

    private final UserRepository userRepository;
    private final UserLeaveHistoryRepository userLeaveHistoryRepository;
    private final UserPushTokenRepository userPushTokenRepository;
    private final ImageUploadService imageUploadService;
    private final PasswordEncoder passwordEncoder;
    private final PushNotificationService pushNotificationService;
    private final EntityManager em;
    private final LevelHistoryRepository levelHistoryRepository;
    private final UserEventInfoRepository userEventInfoRepository ;

    @Override
    public Class<?> getClazz() {
        return User.class;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Cacheable(value = CacheValues.USERS, key = "#id")
    public Optional<UserDto> findUser(long id) {
//        return userRepository.findById(id).map(UserDto::of);
        return userRepository.findById(id).map(UserDto::ofWithPhotoProperty);
    }

    @Transactional(readOnly = true)
    public Optional<PhoneVerifyInfoDto> findPhoneVerifyInfo(long id) {
        return userRepository.findById(id).map(User::getSecurity).map(security -> new PhoneVerifyInfoDto(security.getAuthIdKey(), security.getAuthIdKeySendedAt()));
    }

    @Transactional(readOnly = true)
    public Optional<EmailVerifyInfoDto> findEmailVerifyInfo(long id) {
        return userRepository.findById(id).map(User::getSecurity).map(security -> new EmailVerifyInfoDto(security.getAuthKey(), security.getAuthMailSendedAt()));
    }


    /**
     * 쇼핑몰 프로필 조회용
     * @param id 사용자ID
     * @return Optional<ShopUserDto>
     */
    @SuppressWarnings(value = "unused")
    public Optional<ShopUserDto> findShopUser(long id) {
        return userRepository.findById(id).map(ShopUserDto::of);
    }

    /*
	 * 프로필 관리
	 * 프로필 사진 변경
	 */
    @Transactional(rollbackFor = Exception.class)
    public UserDto updateUserProfileAvatar(User currentUser, UpdateProfileAvatarVo vo) throws InterruptedException, IOException {

        User user = userRepository.getOne(currentUser.getId());
        MultipartFile file = vo.getAvatar();

        if(file != null) {
            ImageUploadService.ImageResourceInfo imageResourceInfo = imageUploadService.upload(FolderDatePatterns.USERS, file);
            user.setImageResource(imageResourceInfo.getImageResource());
        }

        return UserDto.of(user);
    }

    /*
	 * 프로필 관리
	 * 프로필 사진 삭제
	 */
    @Transactional(rollbackFor = Exception.class)
    public UserDto deleteUserProfileAvatar(User currentUser) {

        User user = userRepository.getOne(currentUser.getId());
        user.setImageResource(null);

        return UserDto.of(user);
    }

    /*
	 * 프로필 관리
	 * 닉네임 변경
	 */
    @Transactional(rollbackFor = Exception.class)
    public UserDto updateUserProfileName(User currentUser, UseableVo vo) {

    	if(vo.getNickName() == null) {
    		throw new CommonException(CommonResponseCode.USER_NAME_REGIST_FAIL, null);
    	}

        User user = userRepository.getOne(currentUser.getId());

        if(!vo.getNickName().equals(currentUser.getNickname())) {
            user.setNickname(vo.getNickName());
        }

        return UserDto.of(user);
    }


	/*
	 * 프로필 관리
	 * 비밀번호 변경
	 */
    @Transactional(rollbackFor = Exception.class)
    public UserDto updateUserProfilePassword(User currentUser, PasswordVo vo) {

        User user = userRepository.getOne(currentUser.getId());

        //이메일 가입 사용자
    	if ("email".equals(user.getProvider())) {
    		user.getSecurity().setPassword(passwordEncoder.encode(vo.getPassword()));
    	}

        return UserDto.of(user);
    }


    /*
	 * 프로필 관리
	 * 회원 탈퇴
	 */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteMyProfile(User currentUser, DeleteReasonVo reason) {

        User user = userRepository.getOne(currentUser.getId());

        if(UserStatus.LEAVE.equals(user.getStatus())) {
        	throw new CommonException(CommonResponseCode.USER_ALREADY_LEAVE, null);
        }

        user.setStatus(UserStatus.LEAVE);
        user.setProviderId(user.getProviderId() + "_" + CommonUtils.createUniqueToken());

        userLeaveHistoryRepository.save(new UserLeaveHistory(user));
        user.getSecurity().setDeleteReason(reason.getReason());

        return true;
    }

	/* 프로필관리
	 * 개인정보 - 휴대폰 번호 등록
	 */
    @Transactional(rollbackFor = Exception.class)
    public UserDto updateMyProfilePhone(User currentUser, MyProfilePhoneVo vo) throws CommonException {

    	User user = userRepository.findById(currentUser.getId()).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));

    	if(!currentUser.getUserModifyPhoneHistory().isEmpty()) {
    		UserModifyPhoneHistory userModifyPhoneHistory = currentUser.getUserModifyPhoneHistory().get(currentUser.getUserModifyPhoneHistory().size()-1);

            //휴대폰번호로 인증번호 발송 이력이 있으며 발송 후 AuthorizationMessages.AUTH_ID_KEY_CONFIRM_AT_PLUS_MINUTES 인증번호 확인 시간이 지난 사용자
            if(userModifyPhoneHistory.getSendedAt() != null && ZonedDateTime.now().isAfter(userModifyPhoneHistory.getSendedAt().plusMinutes(AuthorizationMessages.AUTH_ID_KEY_CONFIRM_AT_PLUS_MINUTES))){
            	log.info("인증번호 확인 " + AuthorizationMessages.AUTH_ID_KEY_CONFIRM_AT_PLUS_MINUTES + "분 제한시간 초과");
            	throw new CommonException(CommonResponseCode.USER_AUTHKEY_LATE_FAIL, null);
            }

            //인증번호 미일치
            if(!(vo.getPhone().equals(userModifyPhoneHistory.getPhoneNumber()) && vo.getAuthKey().equals(userModifyPhoneHistory.getAuthKey()))) {
            	throw new CommonException(CommonResponseCode.USER_AUTHKEY_MISS_MATCH, null);
            }

            userModifyPhoneHistory.setAuthStatus(true);
        	user.getSecurity().setPhoneNumber(vo.getPhone());
    	}else {
    		throw new CommonException(CommonResponseCode.FAIL, null);
    	}


        return UserDto.of(user);
    }

	/* 프로필관리
	 * 개인정보 - 휴대폰 번호 등록 - 인증번호 발송
	 */
    @Transactional(rollbackFor = Exception.class)
    public Boolean sendPhoneAuthKey(User currentUser, PhoneAuthKeyVo vo) throws Exception {

        User user = userRepository.findById(currentUser.getId()).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));
        if(!currentUser.getUserModifyPhoneHistory().isEmpty()) {
        	UserModifyPhoneHistory userAuthPhoneHistory = currentUser.getUserModifyPhoneHistory().get(currentUser.getUserModifyPhoneHistory().size()-1);

        	//인증번호 확인 문자 발송 이력이 있으며 발송 후 AuthorizationMessages.AUTH_MAIL_SEND_AT_PLUS_MINUTES(인증메일 재발송 시간)분이 지나지 않은 사용자
        	if(userAuthPhoneHistory.getSendedAt() != null && ZonedDateTime.now().isBefore(userAuthPhoneHistory.getSendedAt().plusMinutes(AuthorizationMessages.AUTH_ID_KEY_SEND_AT_PLUS_MINUTES))){
        		log.info("인증번호 발송 " + AuthorizationMessages.AUTH_ID_KEY_SEND_AT_PLUS_MINUTES + "분내 재시도");

        		//재발송 남은 시간 구하기
        		long authLimitTime = CommonUtils.getAuthRemainingTime(AuthorizationMessages.AUTH_ID_KEY_SEND_AT_PLUS_MINUTES, userAuthPhoneHistory.getSendedAt(), ZonedDateTime.now());

        		throw new CommonException(CommonResponseCode.USER_AUTHKEY_ALREADY_SEND_PHONE.getResultCode(), CommonResponseCode.USER_AUTHKEY_ALREADY_SEND_PHONE.getResultMessage().replace("#RESEND_TIME#", String.valueOf(authLimitTime)), null);
        	}
        }

        int authKey = CommonUtils.createRandomNumber(6);

        UserModifyPhoneHistory modifyPhoneHistory = new UserModifyPhoneHistory();
        modifyPhoneHistory.setPhoneNumber(vo.getPhone());
        modifyPhoneHistory.setAuthKey(String.valueOf(authKey));
        modifyPhoneHistory.setSendedAt(ZonedDateTime.now());

        user.getUserModifyPhoneHistory().add(modifyPhoneHistory);

        userRepository.save(user);
        return sendAuthMtMessage.send(vo.getPhone(), String.valueOf(authKey));
    }

	/* 프로필관리
	 * 개인정보 - 이메일 등록
	 */
    @Transactional(rollbackFor = Exception.class)
    public UserDto updateMyProfileEmail(User currentUser, MyProfileEmailVo vo) throws CommonException {

    	User user = userRepository.findById(currentUser.getId()).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));

    	if(!currentUser.getUserModifyEmailHistory().isEmpty()) {
    		UserModifyEmailHistory userModifyEmailHistory = currentUser.getUserModifyEmailHistory().get(currentUser.getUserModifyEmailHistory().size()-1);

            //이메일로 인증번호 발송 이력이 있으며 발송 후 AuthorizationMessages.AUTH_MAIL_CONFIRM_AT_PLUS_MINUTES(재발송 시간)분이 지난 사용자
            if(userModifyEmailHistory.getSendedAt() != null && ZonedDateTime.now().isAfter(userModifyEmailHistory.getSendedAt().plusMinutes(AuthorizationMessages.AUTH_MAIL_CONFIRM_AT_PLUS_MINUTES))){
            	log.info("인증번호 확인 " + AuthorizationMessages.AUTH_MAIL_CONFIRM_AT_PLUS_MINUTES + "분 제한시간 초과");
            	throw new CommonException(CommonResponseCode.USER_AUTHKEY_LATE_FAIL, null);
            }

            //인증번호 미일치
            if(!(vo.getEmail().equals(userModifyEmailHistory.getEmail()) && vo.getAuthKey().equals(userModifyEmailHistory.getAuthKey()))) {
            	throw new CommonException(CommonResponseCode.USER_AUTHKEY_MISS_MATCH, null);
            }

            userModifyEmailHistory.setAuthStatus(true);
        	user.getSecurity().setEmail(vo.getEmail());
    	}else {
    		throw new CommonException(CommonResponseCode.FAIL, null);
    	}

    	return UserDto.of(user);
    }

    /**
     * 프로필관리
     * 개인정보 - 이메일 등록 - 인증번호 발송
     *
     * @param currentUser 사용자 정보
     * @param vo 사용자 이메일
     * @return boolean 인증번호 발송 결과
     */
    @Transactional(rollbackFor = Exception.class)
    public String getEmailAuthKey(User currentUser, EmailAuthKeyVo vo) throws CommonException {

        User user = userRepository.findById(currentUser.getId()).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));

        if(!currentUser.getUserModifyEmailHistory().isEmpty()) {
        	UserModifyEmailHistory userModifyEmailHistory = currentUser.getUserModifyEmailHistory().get(currentUser.getUserModifyEmailHistory().size()-1);

        	//인증번호 확인 이메일 발송 이력이 있으며 발송 후 AuthorizationMessages.AUTH_MAIL_SEND_AT_PLUS_MINUTES(인증메일 재발송 시간)분이 지나지 않은 사용자
        	if(userModifyEmailHistory.getSendedAt() != null && ZonedDateTime.now().isBefore(userModifyEmailHistory.getSendedAt().plusMinutes(AuthorizationMessages.AUTH_MAIL_SEND_AT_PLUS_MINUTES))){
        		log.info("인증번호 발송 " + AuthorizationMessages.AUTH_MAIL_SEND_AT_PLUS_MINUTES + "분내 재시도");

        		//재발송 남은 시간 구하기
        		long authLimitTime = CommonUtils.getAuthRemainingTime(AuthorizationMessages.AUTH_MAIL_SEND_AT_PLUS_MINUTES, userModifyEmailHistory.getSendedAt(), ZonedDateTime.now());

        		throw new CommonException(CommonResponseCode.USER_AUTHKEY_ALREADY_SEND_PHONE.getResultCode(), CommonResponseCode.USER_AUTHKEY_ALREADY_SEND_PHONE.getResultMessage().replace("#RESEND_TIME#", String.valueOf(authLimitTime)), null);
        	}
        }

        String authKey = String.valueOf(CommonUtils.createRandomNumber(6));

        UserModifyEmailHistory modifyEmailHistory = new UserModifyEmailHistory();
        modifyEmailHistory.setEmail(vo.getEmail());
        modifyEmailHistory.setAuthKey(authKey);
        modifyEmailHistory.setSendedAt(ZonedDateTime.now());

        user.getUserModifyEmailHistory().add(modifyEmailHistory);

        userRepository.save(user);

        return authKey;
    }

    @Transactional
    @SuppressWarnings("unused")
    public UserSettingDto updateAcceptAdNoti(User currentUser, UserSettingVo vo) {
        User user = userRepository.findById(currentUser.getId()).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));
        user.getUserSetting().setAdNotiConfirmedAt(ZonedDateTime.now());
        return UserSettingDto.of(user.getUserSetting());
    }

    private void changeAdNotiAgreement(final User user, final Boolean isAdNotiAgreement) {

        if(isAdNotiAgreement != null && !isAdNotiAgreement.equals(user.getUserSetting().getAdNotiAgreement())) {
            ZonedDateTime currentDateTime = ZonedDateTime.now();
            user.getUserSetting().setAdNotiAgreement(isAdNotiAgreement);
            //마케팅 동의에 따라 푸시 알림도 동일하게 설정
            user.getUserSetting().setAdNotiEnable(isAdNotiAgreement);
            user.getUserSetting().setAdNotiConfirmedAt(currentDateTime);
            user.getUserSetting().setAdNotiUpdatedAt(currentDateTime);
            if(isAdNotiAgreement) {
                pushNotificationService.subscribeTopic(SubscribeTopics.AD_TOPIC, user.getUserPushTokens());
            } else {
                pushNotificationService.unsubscribeTopic(SubscribeTopics.AD_TOPIC, user.getUserPushTokens());
            }
        }
    }

    private void changeAdNotiStatus(final User user, final Boolean isAdNotiEnable) {

        if(isAdNotiEnable != null && user.getUserSetting().isAdNotiEnable() != isAdNotiEnable) {
            user.getUserSetting().setAdNotiEnable(isAdNotiEnable);
            user.getUserSetting().setAdNotiUpdatedAt(ZonedDateTime.now());
            if(Boolean.TRUE.equals(user.getUserSetting().getAdNotiAgreement() && isAdNotiEnable)) {
                pushNotificationService.subscribeTopic(SubscribeTopics.AD_TOPIC, user.getUserPushTokens());
            } else {
                pushNotificationService.unsubscribeTopic(SubscribeTopics.AD_TOPIC, user.getUserPushTokens());
            }
        }
    }

    private void changePostingNotiStatus(final User user, final Boolean isPostingNotiEnable) {

        if(isPostingNotiEnable != null && isPostingNotiEnable != user.getUserSetting().isPostingNotiEnable()) {
            user.getUserSetting().setPostingNotiEnable(isPostingNotiEnable);
            if(isPostingNotiEnable) {
                pushNotificationService.subscribeTopic(SubscribeTopics.POSTING_TOPIC, user.getUserPushTokens());
            } else {
                pushNotificationService.unsubscribeTopic(SubscribeTopics.POSTING_TOPIC, user.getUserPushTokens());
            }
        }
    }

    @SneakyThrows
    private void changeNotiStatus(User user, Boolean isNotiEnable) {
        if(isNotiEnable != null && isNotiEnable != user.getUserSetting().isNotiEnable()) {
            user.getUserSetting().setNotiEnable(isNotiEnable);

            List<UserPushToken> pushTokens = user.getUserPushTokens();
            if(!pushTokens.isEmpty()) {
                CompletableFuture<Void> completableFuture;
                if (isNotiEnable) {
                    completableFuture = CompletableFuture.allOf(
                            CompletableFuture.supplyAsync(() -> pushNotificationService.subscribeTopic(SubscribeTopics.DEFAULT_TOPIC, pushTokens)),
                            CompletableFuture.supplyAsync(() -> pushNotificationService.subscribeTopic(SubscribeTopics.POSTING_TOPIC, pushTokens)),
                            CompletableFuture.supplyAsync(() -> pushNotificationService.subscribeTopic(SubscribeTopics.AD_TOPIC, pushTokens))
                    );
                } else {
                    completableFuture = CompletableFuture.allOf(
                            CompletableFuture.supplyAsync(() -> pushNotificationService.unsubscribeTopic(SubscribeTopics.DEFAULT_TOPIC, pushTokens)),
                            CompletableFuture.supplyAsync(() -> pushNotificationService.unsubscribeTopic(SubscribeTopics.POSTING_TOPIC, pushTokens)),
                            CompletableFuture.supplyAsync(() -> pushNotificationService.unsubscribeTopic(SubscribeTopics.AD_TOPIC, pushTokens))
                    );
                }
                completableFuture.get(5, TimeUnit.SECONDS);
            }
        }
    }


    @SneakyThrows
    @Transactional
    public UserSettingDto updateSetting(User currentUser, UserSettingVo vo) {
        User user = userRepository.findById(currentUser.getId()).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));

        // 클라이언트에서 보내는 설정값으로 이용한다.
        Boolean isNotiEnabled = vo.getNotiEnable();
        changeNotiStatus(user, isNotiEnabled);

        Boolean isAdNotiAgreement = vo.getAdNotiAgreement();
        changeAdNotiAgreement(user, isAdNotiAgreement);

        Boolean isAdNotiEnable = vo.getAdNotiEnable();
        changeAdNotiStatus(user, isAdNotiEnable);

        Boolean isPostingNotiEnable = vo.getPostingNotiEnable();
        changePostingNotiStatus(user, isPostingNotiEnable);

        if(vo.getCommentNotiEnable() != null) {
            user.getUserSetting().setCommentNotiEnable(vo.getCommentNotiEnable());
        }

        if(vo.getReplyNotiEnable() != null) {
            user.getUserSetting().setReplyNotiEnable(vo.getReplyNotiEnable());
        }

        if(vo.getLikeNotiEnable() != null) {
            user.getUserSetting().setLikeNotiEnable(vo.getLikeNotiEnable());
        }

        if(vo.getMentionNotiEnable() != null) {
            user.getUserSetting().setMentionNotiEnable(vo.getMentionNotiEnable());
        }

        if(vo.getShopNotiEnable() != null) {
            user.getUserSetting().setShopNotiEnable(vo.getShopNotiEnable());
        }

        if(vo.getJoinCommentNotiEnable() != null) {
            user.getUserSetting().setJoinCommentNotiEnable(vo.getJoinCommentNotiEnable());
        }

        if(vo.getAdoptNotiEnable() != null) {
            user.getUserSetting().setAdoptNotiEnable(vo.getAdoptNotiEnable());
        }

        if(vo.getBadgeNotiEnable() != null) {
            user.getUserSetting().setBadgeNotiEnable(vo.getBadgeNotiEnable());
        }

        return UserSettingDto.of(user.getUserSetting());
    }

    /**
     * 댓글수에 따라 레벨 뱃지를 변경한다.
     * @param owner 사용자
     * @param commentCount 댓글수
     */
    public void updateLevel(User owner, int commentCount) {
        LevelContainer.LevelInfo levelInfo = LevelContainer.getInstance().getLevel(commentCount);

        if(levelInfo.getLevel() > owner.getLevel()) {
            userRepository.updateLevel(owner.getId(), levelInfo.getLevel());

            LevelHistory levelHistory = new LevelHistory(null, levelInfo.getLevel(), LevelHistory.HistoryType.levelup, owner, ZonedDateTime.now(), commentCount);
            levelHistoryRepository.save(levelHistory);
        } else if(commentCount % 10000 == 0 ) {

            LevelHistory levelHistory = new LevelHistory(null, levelInfo.getLevel(), LevelHistory.HistoryType.tenthausand, owner, ZonedDateTime.now(), commentCount);
            levelHistoryRepository.save(levelHistory);
        }
    }

    @ToString(of = {"userId", "osType", "token", "badge"})
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotiRecipientInfo {

        public NotiRecipientInfo(@NonNull Long userId, @NonNull String osType, @NonNull String token, long badge, long count) {
            this.userId = userId;
            this.osType = osType;
            this.token = token;
            this.badge = badge;
            this.count = count;
        }

        @NonNull
        private Long userId;
        @NonNull
        private String osType;
        @NonNull
        private String token;

        private long badge;

        private long count;

        private boolean isTopic;
    }

    public List<NotiRecipientInfo> findEnableFcmTokensByUserIds(Set<Long> userIds) {
        if(CollectionUtils.isEmpty(userIds)) return Collections.emptyList();
        return userRepository.findUserPushTokenWithOsTypeAndBadges(userIds).stream().map(tuple -> new NotiRecipientInfo( ((BigInteger)tuple.get(0)).longValue(), (String)tuple.get(1),(String)tuple.get(2), ((BigInteger)tuple.get(3)).longValue(), ((BigInteger)tuple.get(4)).longValue())).collect(Collectors.toList());
    }

    /**
     * 사용자 osType 별 하나만 관리
     * @param userInfo 사용자정보
     * @param userToken 사용자 토큰
     * @param deviceId 디바이스ID
     * @param osType osType (android, ios)
     * @return UserPushToken (토큰 entity)
     */
    @SuppressWarnings({"unused", "UnusedReturnValue"})
    @Transactional(rollbackFor = Exception.class)
    public UserPushToken createUserPushToken(User userInfo, String userToken, String deviceId, String osType) {

        // 해당 조합 (사용자 + deviceId 확인)으로 사용자 푸쉬 토큰이 있는지 체크
        Optional<UserPushToken> pushTokenOptional = userPushTokenRepository.findByUserIdAndOsType(userInfo.getId(), osType);

        UserPushToken userPushToken = new UserPushToken();

        // 만약 해당 사용자 + deviceId 가 존재 한다면
        if(pushTokenOptional.isPresent()) {

            // 신규 토큰으로 업데이트
            userPushToken = pushTokenOptional.get();
            userPushToken.setToken(userToken);

            // 신규 생성 케이스
        } else {
            userPushToken.setToken(userToken);
            userPushToken.setDeviceId(deviceId);
            userPushToken.setUser(userInfo);
        }

        // OS 타입 설정
        userPushToken.setOsType(osType);

        userPushTokenRepository.save(userPushToken);

        // 해당 PUSH 토큰 + 사용자 ID 중에, 요청 device_id를 제외한 푸쉬토큰 제거
        userPushTokenRepository.deleteDuplicateToken(userPushToken.getUser().getId(), userPushToken.getToken(), osType);

        if(Boolean.TRUE.equals(userInfo.getUserSetting().getAdNotiAgreement()) && userInfo.getUserSetting().isAdNotiEnable()) {
            pushNotificationService.subscribeTopic(SubscribeTopics.AD_TOPIC, Collections.singletonList(userPushToken));
        }

        if(userInfo.getUserSetting().isNotiEnable()) {
            pushNotificationService.subscribeTopic(SubscribeTopics.DEFAULT_TOPIC, Collections.singletonList(userPushToken));
        }

        if(userInfo.getUserSetting().isPostingNotiEnable()) {
            pushNotificationService.subscribeTopic(SubscribeTopics.POSTING_TOPIC, Collections.singletonList(userPushToken));
        }

        return userPushToken;

    }

    /**
     * 사용자 디바이스 PUSH 토큰 삭제
     * 요청 사용자의 device_id로 등록된 push token row 삭제
     *
     * @param userInfo 사용자 정보
     * @return boolean 삭제 결과
     */
    @SuppressWarnings({"unused", "UnusedReturnValue"})
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserPushToken(User userInfo, String deviceId) {

        UserPushToken userPushToken = userPushTokenRepository.findByUserIdAndDeviceId(userInfo.getId(), deviceId)
                .orElseThrow(() -> new CommonException(CommonResponseCode.USER_NOT_EXIST.getResultCode(), null, CommonResponseCode.USER_NOT_EXIST.getResultMessage()));

        pushNotificationService.unsubscribeTopic(SubscribeTopics.AD_TOPIC, Collections.singletonList(userPushToken));
        pushNotificationService.unsubscribeTopic(SubscribeTopics.DEFAULT_TOPIC, Collections.singletonList(userPushToken));
        pushNotificationService.unsubscribeTopic(SubscribeTopics.POSTING_TOPIC, Collections.singletonList(userPushToken));

        userPushTokenRepository.delete(userPushToken);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    @Cacheable(value = CacheValues.USERS, key = "{'search', #sinceId, #maxId, #count, #query}")
    public List<UserDto> searchTimeline(Long sinceId, Long maxId, int count, String query) {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(User.class).get();

        BooleanJunction<?> booleanJunction = qb.bool();
        TimelineJunction.addJunction(booleanJunction, sinceId, maxId);

        if(query != null && !query.isEmpty()) {
            String nicknameField = NICKNAME_FIELD;
            BooleanJunction<?> targetJunction = qb.bool();
            targetJunction.should(qb.keyword().wildcard().onField(nicknameField).matching(query).createQuery());
            targetJunction.should(qb.keyword().wildcard().onField(nicknameField).matching(query + "*").createQuery());
            targetJunction.should(qb.keyword().wildcard().onField(nicknameField).matching("*" + query).createQuery());
            targetJunction.should(qb.keyword().wildcard().onField(nicknameField).matching("*" + query + "*").createQuery());
            booleanJunction.must(targetJunction.createQuery());
        }

        booleanJunction.must(qb.keyword().onField("status").matching(UserStatus.NORMAL).createQuery());

        Query luceneQuery = booleanJunction.createQuery();
        Sort sort = qb.sort().byScore().desc().andByField("activity.commentCount").desc().createSort();
        javax.persistence.Query persistenceQuery =
                fullTextEntityManager.createFullTextQuery(luceneQuery, User.class)
                        .setFirstResult(0)
                        .setMaxResults(count).setSort(sort);

        List<User> users = persistenceQuery.getResultList();
        return users.stream().map(UserDto::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShippingDto findAddress(User currentUser) {

        Optional<UserEventInfo> optionalUserEventInfo = userEventInfoRepository.findByUser(currentUser);
        return optionalUserEventInfo.map(ShippingDto::of).orElseThrow(() -> new CommonException(CommonResponseCode.FAIL.getResultCode(), "배송지 정보를 찾을 수 없습니다."));
    }

    @Transactional
    public ShippingDto updateAddress(User currentUser, UserAddressVo vo) {
        User user = userRepository.findById(currentUser.getId()).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));

        Optional<UserEventInfo> optionalUserEventInfo = userEventInfoRepository.findByUser(currentUser);
        UserEventInfo userEventInfo = optionalUserEventInfo.orElseGet(() -> UserEventInfo.builder().build());

        userEventInfo.setRoadAddress(vo.getRoadAddress());
        userEventInfo.setDetailAddress(vo.getDetailAddress());
        userEventInfo.setPostCode(vo.getPostCode());
        userEventInfo.setName(vo.getName());
        userEventInfo.setPhone1(vo.getPhone1());
        userEventInfo.setPhone2(vo.getPhone2());
        user.setEventInfo(userEventInfo);

        userEventInfoRepository.save(userEventInfo);

        return ShippingDto.of(userEventInfo);
    }

    @SuppressWarnings({"java:S3740", "unchecked"})
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    @Cacheable(value = CacheValues.PHOTO_WRITERS, key = "{'search', #className, #sinceId, #maxId, #offset, #count, #orderby, #day}")
    public List<UserDto> searchWriterTimeline(Class<?> className, Long sinceId, Long maxId, int offset, int count, String orderby, Integer day) {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(className).get();

        BooleanJunction<?> booleanJunction = qb.bool();
        booleanJunction.must(NumericRangeQuery.newLongRange("id", Long.MIN_VALUE, Long.MAX_VALUE, true, true));

        if(orderby.equals("id") || orderby.equals("recent")){
            TimelineJunction.addJunction(booleanJunction, sinceId, maxId);
        }
        else{
            booleanJunction.must(qb.keyword().onField("status").matching(UserStatus.NORMAL).createQuery());
            booleanJunction.must(NumericRangeQuery.newIntRange("totalPhotosCnt", 1, Integer.MAX_VALUE, true, true));
        }

        Query luceneQuery = booleanJunction.createQuery();
        Sort sort;
        if(orderby.equals("photoCount")) {
            sort = qb.sort().byField("totalPhotosCnt").desc().createSort();
        } else if(orderby.equals("photoLikeCount")) {
            sort = qb.sort().byField("totalPhotosLikeCnt").desc().createSort();
        } else {
            sort = qb.sort().byField("id").desc().createSort();
        }

        javax.persistence.Query persistenceQuery;
        List<User> users;
        if(orderby.equals("id") || orderby.equals("recent")){
            persistenceQuery =
                fullTextEntityManager.createFullTextQuery(luceneQuery, className)
                .setMaxResults(count).setSort(sort);
            List<PhotoRecentWriter> resultList = persistenceQuery.getResultList();
            users = resultList.stream().map(PhotoRecentWriter::getRelation).map(UserPostingRelation::getUser).collect(Collectors.toList());

        } else {
            persistenceQuery =
                fullTextEntityManager.createFullTextQuery(luceneQuery, className)
                    .setFirstResult(offset)
                    .setMaxResults(count).setSort(sort);

            users = persistenceQuery.getResultList();
        }

        return users.stream().map(UserDto::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheValues.PHOTO_WRITERS, key = "{'find', #className, #sinceId, #maxId, #count}")
    public List<PhotoWriterDto> findWriterTimeline(Class<?> className, Long sinceId, Long maxId, int count) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<PhotoRecentWriter> root = query.from(PhotoRecentWriter.class);

        Subquery<Number> sq = query.subquery(Number.class);
        Root<PhotoRecentWriter> root2 = sq.from(PhotoRecentWriter.class);    // SW_VERSION_DELTA_TB
        Expression<Number> id2 = cb.max(root2.get("id"));
        Path<User> userPath = root2.get("relation").get("user");
        sq.select(id2);
        sq.groupBy(userPath);

        List<Predicate> predicates = new ArrayList<>();
        if (maxId != null && maxId > 0) predicates.add(cb.le(root.get("id"), maxId));
        if (sinceId != null && sinceId > 0) predicates.add(cb.gt(root.get("id"), sinceId));
        predicates.add(root.get("id").in(sq));

        query.multiselect(root.get("relation").get("user"), root.get("id"));
        query.where(predicates.toArray(new Predicate[]{})).orderBy(cb.desc(root.get("id")));

        List<Tuple> list = em.createQuery(query).setMaxResults(count).getResultList();

        return list.stream().map(tuple -> {
            PhotoWriterDto dto = new PhotoWriterDto(UserDto.of(tuple.get(0, User.class)), Collections.emptyList());
            dto.setTimelineId(tuple.get(1, Number.class).longValue());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void userIndexing(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new FlabookGlobalException("not found entity : " + id));

        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
        FullTextSession session = fullTextEntityManager.unwrap(FullTextSession.class);
        session.index(user);
        session.flush();
        session.clear();
    }

}

