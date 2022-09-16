package com.msquare.flabook.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.api.exceptions.AuthenticationException;
import com.msquare.flabook.api.service.AuthorizationService;
import com.msquare.flabook.api.service.SendMailAsyncService;
import com.msquare.flabook.api.service.SnsProviderService;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.swagger.response.UserInfoAndUserTokenDto;
import com.msquare.flabook.enumeration.MailType;
import com.msquare.flabook.enumeration.UserProfileProviderType;
import com.msquare.flabook.enumeration.UserStatus;
import com.msquare.flabook.exception.CommonException;
import com.msquare.flabook.form.CreateMailVo;
import com.msquare.flabook.form.auth.*;
import com.msquare.flabook.models.SnsUserProfile;
import com.msquare.flabook.models.User;
import com.msquare.flabook.repository.UserRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.ZonedDateTime;

@Slf4j

@Api(tags = {"1. User 인증"})

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/v2/auth")
public class AuthorizationController {
	private final AuthorizationService authorizationService;
	private final SendMailAsyncService sendMailAsyncService;
    private final SnsProviderService snsProviderService;
	private final UserRepository userRepository;

    @PostMapping(path = "/key")
    @ApiOperation(value = "이메일 가입, 인증번호 확인")
    public CommonResponse<UserInfoAndUserTokenDto> confirmAuthStatus(@RequestBody AuthKeyVo requestAuthUser) {

    	return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), authorizationService.confirmAuthKey(requestAuthUser.getUserInfoId(), requestAuthUser.getAuthKey()), CommonResponseCode.SUCCESS.getResultMessage());
    }

    @PostMapping(path = "/mail")
    @ApiOperation(value = "이메일 가입, 이메일 인증번호 발송", notes = "이메일로 인증번호 발송 후, [POST]/v2/auth/key 인증번호 확인 필요")
    public CommonResponse<Boolean> sendAuthonMail(@RequestBody UserIdVo vo) {
    	User user = authorizationService.getAuthorizationMail(vo.getUserInfoId());
		if(user == null) {
			throw new CommonException(CommonResponseCode.USER_EMAIL_LATE_FAILL, null);
		}

		//인증 메일 발송
		CreateMailVo createMailVo = CreateMailVo.builder()
				.authKey(user.getSecurity().getAuthKey())
				.nickName(user.getNickname())
				.email(user.getProviderId())
				.type(MailType.join)
				.build();

		sendMailAsyncService.sendMail(createMailVo);
		return new CommonResponse<>(CommonResponseCode.SUCCESS, true);
    }

    //Unghee
    @PostMapping(path = "/join")
    @ApiOperation(value = "이메일 가입", notes = "API호출 후, [GET]/v2/auth/mail 인증번호 이메일 발송 필요")
    public CommonResponse<UserInfoAndUserTokenDto> join(@RequestBody @Valid EmailSignUpVo requestAuthUser) {
    	return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), authorizationService.joinUser(requestAuthUser.getEmail(), requestAuthUser.getNickName(), requestAuthUser.getPassword(), "email"), CommonResponseCode.SUCCESS.getResultMessage());
    }

    @PostMapping(path = "/login")
    @ApiOperation(value = "이메일 로그인")
    public CommonResponse<UserInfoAndUserTokenDto> loginByEmail(@RequestBody @Valid EmailLoginVo requestAuthUser) {
    	return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), authorizationService.loginUser(requestAuthUser.getEmail(), requestAuthUser.getPassword(), "email"), CommonResponseCode.SUCCESS.getResultMessage());
    }

    @PostMapping(path = "/token")
    @ApiOperation(value = "AccessToken 재발급", notes = "resultCode[9023], 만료된 토큰 확인 시 해당 API 호출하여 RefreshToken으로 AccessToken 재발급")
    public CommonResponse<UserInfoAndUserTokenDto> doUpdateAccessTokenByRefreshToken(@RequestBody @Valid RefreshTokenVo vo) {
    	return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), authorizationService.updateAccessTokenByRefreshToken(vo.getRefreshToken()), CommonResponseCode.SUCCESS.getResultMessage());
    }

    @GetMapping(path = "/users")
    @ApiOperation(value = "이름 또는 닉네임, 이메일 중복 확인", notes = "String nickName OR String email")
    public CommonResponse<Boolean> confirmUserName(@Valid UseableVo requestAuthUser) {
    	Boolean isPresent;
    	if(requestAuthUser.getNickName() != null && !requestAuthUser.getNickName().isEmpty()) {
    		isPresent = authorizationService.confirmUserByUserName(requestAuthUser.getNickName(), UserStatus.LEAVE);
    		return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), !isPresent, "사용 가능한 이름 또는 닉네임입니다.");
    	} else if(requestAuthUser.getEmail() != null && !requestAuthUser.getEmail().isEmpty()) {
    		isPresent = authorizationService.confirmUserByEmail(requestAuthUser.getEmail());
    		return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), !isPresent, "사용 가능한 이메일입니다.");
    	}
    	return new CommonResponse<>(CommonResponseCode.PARAMETER_ERROR.getResultCode(), false, CommonResponseCode.PARAMETER_ERROR.getResultMessage());
    }

    @PostMapping(path = "/join/{provider}")
    @ApiOperation(value = "SNS 가입", notes = "@PathVariable String provider\nString accessToken, String userName")
    public CommonResponse<UserInfoAndUserTokenDto> signupByProvider(@PathVariable String provider, @RequestBody @Valid SignUpByProviderVo requestAuthUser) {
        SnsUserProfile profile = snsProviderService.getSnsUserProfile(requestAuthUser.getAccessToken(), provider);
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), authorizationService.joinUser(profile.getId(), requestAuthUser.getNickName(), null, provider), CommonResponseCode.SUCCESS.getResultMessage());
    }

    @PostMapping(path = "/login/{provider}")
    @ApiOperation(value = "SNS 로그인", notes = "@PathVariable String provider\nString accessToken")
    public CommonResponse<UserInfoAndUserTokenDto> loginByProvider(@PathVariable String provider, @RequestBody @Valid LoginByProviderVo requestAuthUser) {
    	SnsUserProfile profile = snsProviderService.getSnsUserProfile(requestAuthUser.getAccessToken(), provider);
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), authorizationService.loginUser(profile.getId(), null, provider), CommonResponseCode.SUCCESS.getResultMessage());
    }

    @PostMapping(path = "/login/phone")
    @ApiOperation(value = "휴대폰번호 로그인")
    public CommonResponse<UserInfoAndUserTokenDto> loginByPhone(@RequestBody @Valid LoginByPhoneVo requestAuthUser) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), authorizationService.loginUserByPhone(requestAuthUser), CommonResponseCode.SUCCESS.getResultMessage());
    }

    @PostMapping(path = "/phone")
    @ApiOperation(value = "휴대폰번호 로그인 인증 확인 문자 발송")
    public CommonResponse<Boolean> sendAuthIdKey(@RequestBody ConfirmByPhoneVo requestAuthUser) throws Exception {
    	return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), authorizationService.sendAuthIdKey(requestAuthUser), CommonResponseCode.SUCCESS.getResultMessage());
    }

	@PostMapping(path = "/token/access/{id}")
	@ApiOperation(value = "액세스 토큰 만료 시키기")
	public CommonResponse<Boolean> doUpdateAccessTokenByRefreshTokenTest(@PathVariable long id) throws AuthenticationException {
	    authorizationService.expireUserAccessToken(id);
		return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), null, CommonResponseCode.SUCCESS.getResultMessage());
	}

	@PostMapping(path = "/token/refresh/{id}")
	@ApiOperation(value = "리프레쉬 토큰 만료 시키기")
	public CommonResponse<Boolean> doUpdateAccessTokenByRefreshTokenTest2(@PathVariable long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new CommonException(CommonResponseCode.UNKNOWN_RESPONSE_TYPE, null));
		user.getSecurity().setRefreshTokenExpireAt(ZonedDateTime.now().minusMinutes(1000));
		userRepository.save(user);
		return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), null, CommonResponseCode.SUCCESS.getResultMessage());
	}



	/*비밀번호 찾기*/
    @GetMapping(path = "/users/password/reset")
    @ApiOperation(value = "사용자 비밀번호 찾기, 비밀번호 변경 개인화 URL 전송", notes = "String email")
    public CommonResponse<Boolean> modifyPasswordSendMail(@Valid EmailVo vo) {

    	String password = authorizationService.modifyPasswordSendMail(vo.getEmail(), UserProfileProviderType.email.name());
		if(password == null) {
			throw new CommonException(CommonResponseCode.USER_EMAIL_LATE_FAILL, vo.getEmail());
		}

		//인증 메일 발송
		CreateMailVo createMailVo = CreateMailVo.builder()
				.password(password)
				.email(vo.getEmail())
				.type(MailType.resetPassword)
				.build();

		sendMailAsyncService.sendMail(createMailVo);
		return new CommonResponse<>(CommonResponseCode.SUCCESS, true);

    }

    @PostMapping(path = "/users/password/modify/check")
    @ApiOperation(value = "비밀번호 변경 URL에서 호출, 비밀 번호 변경")
    public CommonResponse<Boolean> modifyPasswordUrlCheck(@RequestBody @Valid PasswordFindModifyCheckVo vo) {
    	if(vo.getAuthKey() == null) {
    		log.info("유효하지 않은 URL 입니다. [authKey is Null]");
    		throw new CommonException(CommonResponseCode.FAIL.getResultCode(), "유효하지 않은 URL입니다.", false);
    	}
    	return new CommonResponse<>(CommonResponseCode.SUCCESS, authorizationService.modifyPasswordUrlCheck(vo.getAuthKey()));

    }

    @PostMapping(path = "/users/password/modify")
    @ApiOperation(value = "비밀번호 변경 URL에서 호출, 비밀 번호 변경")
    public CommonResponse<Boolean> modifyPassword(@RequestBody @Valid PasswordFindModifyVo vo) {
    	authorizationService.modifyPassword(vo.getPassword(), vo.getAuthKey(), UserProfileProviderType.email.name());
    	return new CommonResponse<>(CommonResponseCode.SUCCESS, true);
    }

    /*비밀번호 찾기*///
}

