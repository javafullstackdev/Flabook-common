package com.msquare.flabook.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.configurations.FlabookPasswordEncoder;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.UserDto;
import com.msquare.flabook.dto.UserWithSecurityDto;
import com.msquare.flabook.dto.swagger.response.UserInfoAndUserTokenDto;
import com.msquare.flabook.enumeration.UserProfileProviderType;
import com.msquare.flabook.exception.CommonException;
import com.msquare.flabook.form.auth.LoginByPhoneInShopVo;
import com.msquare.flabook.models.User;
import com.msquare.flabook.repository.UserRepository;
import com.msquare.flabook.repository.UserSecurityRepository;
import com.msquare.flabook.util.SendAuthMtMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopAuthorizationService {

	@SuppressWarnings("unused")
	private final SendAuthMtMessage sendAuthMtMessage; //문자 메세지 발송, 지우지 마세요.

	@NonNull
	@SuppressWarnings("unused")
	private final UserSecurityRepository userSecurityRepository;

	@NonNull
    private final UserRepository userRepository;

	@NonNull
	@SuppressWarnings("unused")
	private final CacheManager cacheManager;

	@Lazy
	@Autowired
    private  PasswordEncoder passwordEncoder;

	@SuppressWarnings("unused")
    @Value("${infobank.token.accessToken.expiredAt.minutes:1440}")
	private int accessTokenExpireMinutes;

	@SuppressWarnings("unused")
    @Value("${infobank.token.refreshToken.expiredAt.months:1}")
	private int refreshTokenExpireMonths;


    //이메일 또는 SNS 로그인
    @Transactional(rollbackFor = Exception.class)
    public UserInfoAndUserTokenDto loginUserInShop(String providerId, String password, String provider) {

    	log.info("loginUserInShop - [{}][{}]", provider, providerId);
    	User user = userRepository.findByProviderIdAndProvider(providerId, provider).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));

    	//이메일 가입 사용자 && 비밀번호 미일치
    	if ("email".equals(provider) && !passwordEncoder.matches(password, user.getSecurity().getPassword())) {
    		throw new CommonException(CommonResponseCode.USER_PASSWORD_MISS_MATCH, null);
    	}

    	//인증번호 미입력 사용자
    	if(user.getSecurity().getAuthStatus() != null && !user.getSecurity().getAuthStatus()) {
			UserInfoAndUserTokenDto dto = UserInfoAndUserTokenDto.of(UserDto.of(user), null, null, null, null, null, null);
    		throw new CommonException(CommonResponseCode.USER_AUTHKEY_CONFIRM_NEED, dto);
    	}

        return UserInfoAndUserTokenDto.of(UserWithSecurityDto.of(user), user.getSecurity().getAccessToken(), user.getSecurity().getAccessTokenExpireAt(), user.getSecurity().getRefreshToken(), user.getSecurity().getRefreshTokenExpireAt(), user.getUserSetting().getAdNotiAgreement(), user.getUserSetting().getAdNotiConfirmedAt());
    }

    //쇼핑몰 - 핸드폰 로그인
    @Transactional(rollbackFor = Exception.class)
    public UserInfoAndUserTokenDto loginUserByPhoneInShop(LoginByPhoneInShopVo loginUser) {

    	log.info("loginUserByPhoneInShop - [{}][{}]", loginUser.getAuthId(), "phone");
    	/*기존 모야모 로그인과 동일하게*/
    	User user = userRepository.findByProviderIdAndProvider(loginUser.getAuthId(), UserProfileProviderType.phone.name()).orElseThrow(()-> new CommonException(CommonResponseCode.USER_NOT_EXIST, null));

    	//기존 모야모 패스워드 확인
    	String password = FlabookPasswordEncoder.hashedPassword(loginUser.getNickName(), user.getSecurity().getSalt());
    	if(!user.getSecurity().getPassword().equals(password)) {
    		throw new CommonException(CommonResponseCode.USER_NAME_LOGIN_FAIL, null);
    	}

        return UserInfoAndUserTokenDto.of(UserWithSecurityDto.of(user), user.getSecurity().getAccessToken(), user.getSecurity().getAccessTokenExpireAt(), user.getSecurity().getRefreshToken(), user.getSecurity().getRefreshTokenExpireAt(), user.getUserSetting().getAdNotiAgreement(), user.getUserSetting().getAdNotiConfirmedAt());
    }
}

