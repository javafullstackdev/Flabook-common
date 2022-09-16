package com.msquare.flabook.controller;

import javax.validation.Valid;

import com.msquare.flabook.api.service.AuthorizationService;
import com.msquare.flabook.api.service.SendMailAsyncService;
import com.msquare.flabook.enumeration.MailType;
import com.msquare.flabook.enumeration.UserStatus;
import com.msquare.flabook.exception.CommonException;
import com.msquare.flabook.form.CreateMailVo;
import com.msquare.flabook.form.auth.*;
import com.msquare.flabook.models.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.api.service.ShopAuthorizationService;
import com.msquare.flabook.api.service.SnsProviderService;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.swagger.response.UserInfoAndUserTokenDto;
import com.msquare.flabook.models.SnsUserProfile;

@Slf4j
@Api(tags = {"1. User 인증"})
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/auth")
public class ShopAuthorizationController {
	private final ShopAuthorizationService shopAuthorizationService;
    private final SnsProviderService snsProviderService;

    private final AuthorizationService authorizationService;
    private final SendMailAsyncService sendMailAsyncService;

    @PostMapping(path = "/shop/login")
    @ApiOperation(value = "이메일 로그인")
    public CommonResponse<UserInfoAndUserTokenDto> loginByEmail(@RequestBody @Valid EmailLoginVo requestAuthUser) {

    	return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), shopAuthorizationService.loginUserInShop(requestAuthUser.getEmail(), requestAuthUser.getPassword(), "email"), CommonResponseCode.SUCCESS.getResultMessage());
    }

    @PostMapping(path = "/shop/login/{provider}")
    @ApiOperation(value = "SNS 로그인", notes = "@PathVariable String provider\nString accessToken")
    public CommonResponse<UserInfoAndUserTokenDto> loginByProvider(@PathVariable String provider, @RequestBody @Valid LoginByProviderVo requestAuthUser) {
    	SnsUserProfile profile = snsProviderService.getSnsUserProfile(requestAuthUser.getAccessToken(), provider);
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), shopAuthorizationService.loginUserInShop(String.valueOf(profile.getId()), null, provider), CommonResponseCode.SUCCESS.getResultMessage());
    }


    @PostMapping(path = "/shop/login/phone")
    @ApiOperation(value = "쇼핑몰 - 휴대폰번호 로그인")
    public CommonResponse<UserInfoAndUserTokenDto> loginByPhoneInShop(@RequestBody @Valid LoginByPhoneInShopVo requestAuthUser) {
        System.out.println("requestAuthUser = " + requestAuthUser);

    	return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), shopAuthorizationService.loginUserByPhoneInShop(requestAuthUser), CommonResponseCode.SUCCESS.getResultMessage());
    }

    //가입
    @CrossOrigin(origins = {"http://www.flabook.shop", "http://flabook.shop"})
    @PostMapping(path = "/shop/join")
    @ApiOperation(value = "이메일 가입", notes = "API호출 후, [GET]/v2/auth/mail 인증번호 이메일 발송 필요")
    public CommonResponse<UserInfoAndUserTokenDto> join(@RequestBody @Valid EmailSignUpVo requestAuthUser) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), authorizationService.joinUser(requestAuthUser.getEmail(), requestAuthUser.getNickName(), requestAuthUser.getPassword(), "email"), CommonResponseCode.SUCCESS.getResultMessage());
    }

    @CrossOrigin(origins = {"http://www.flabook.shop", "http://flabook.shop"})
    @GetMapping(path = "/shop/users")
    @ApiOperation(value = "이름 또는 닉네임, 이메일 중복 확인", notes = "String nickName OR String email")
    public CommonResponse<Boolean> confirmUserName(@Valid UseableVo requestAuthUser) {
        System.out.println("requestAuthUser = " + requestAuthUser);
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

    @CrossOrigin(origins = {"http://www.flabook.shop", "http://flabook.shop"})
    @PostMapping(path = "/shop/join/{provider}")
    @ApiOperation(value = "SNS 가입", notes = "@PathVariable String provider\nString accessToken, String userName")
    public CommonResponse<UserInfoAndUserTokenDto> signupByProvider(@PathVariable String provider, @RequestBody @Valid SignUpByProviderVo requestAuthUser) {
        SnsUserProfile profile = snsProviderService.getSnsUserProfile(requestAuthUser.getAccessToken(), provider);
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), authorizationService.joinUser(profile.getId(), requestAuthUser.getNickName(), null, provider), CommonResponseCode.SUCCESS.getResultMessage());
    }

    @CrossOrigin(origins = {"http://www.flabook.shop", "http://flabook.shop"})
    @PostMapping(path = "/shop/key")
    @ApiOperation(value = "이메일 가입, 인증번호 확인")
    public CommonResponse<UserInfoAndUserTokenDto> confirmAuthStatus(@RequestBody AuthKeyVo requestAuthUser) {

        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), authorizationService.confirmAuthKey(requestAuthUser.getUserInfoId(), requestAuthUser.getAuthKey()), CommonResponseCode.SUCCESS.getResultMessage());
    }

    @CrossOrigin(origins = {"http://www.flabook.shop", "http://flabook.shop"})
    @PostMapping(path = "/shop/mail")
    @ApiOperation(value = "이메일 가입, 이메일 인증번호 발송", notes = "이메일로 인증번호 발송 후, [POST]/v2/auth/key 인증번호 확인 필요")
    public CommonResponse<Boolean> sendAuthonMail(@RequestBody UserIdVo vo) {
        System.out.println("이메일 가입 vo = " + vo);
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
}

