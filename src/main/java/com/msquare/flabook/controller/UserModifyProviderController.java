package com.msquare.flabook.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.api.service.SnsProviderService;
import com.msquare.flabook.api.service.UserModifyProviderService;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.UserDto;
import com.msquare.flabook.dto.UserModifyProviderLoginDto;
import com.msquare.flabook.enumeration.UserProfileProviderType;
import com.msquare.flabook.form.auth.EmailSignUpVo;
import com.msquare.flabook.form.auth.MyProfileEmailVo;
import com.msquare.flabook.form.auth.SignUpByProviderVo;
import com.msquare.flabook.models.SnsUserProfile;
import com.msquare.flabook.models.User;
import springfox.documentation.annotations.ApiIgnore;

@SuppressWarnings("java:S4684")
@Slf4j
@Api(tags = {"3. 계정연동"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/modify")
public class UserModifyProviderController {

    private final UserModifyProviderService userModifyProviderService;
    private final SnsProviderService snsProviderService;

	@PostMapping(path = "/providers/{provider}")
    @ApiOperation(value = "계정연동 - 기존 휴대폰 로그인 사용자 -> sns 전환")
    public CommonResponse<UserModifyProviderLoginDto> doUpdateLoginProviderBySns(@ApiIgnore User currentUser, @PathVariable UserProfileProviderType provider, @RequestBody @Valid SignUpByProviderVo vo) {

    	SnsUserProfile snsUserProfile = snsProviderService.getSnsUserProfile(vo.getAccessToken(), provider.name());

        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), userModifyProviderService.updateUserLoginProviderBySns(currentUser, String.valueOf(snsUserProfile.getId()), currentUser.getNickname(), null, provider), CommonResponseCode.SUCCESS.getResultMessage());
    }

	@PostMapping(path = "/providers/mail/auth")
    @ApiOperation(value = "계정연동 - 기존 휴대폰 로그인 사용자 - email 계정 등록 및 인증 번호 발송")
    public CommonResponse<UserDto> doSendLoginProviderAuthEmail(@ApiIgnore User currentUser, @RequestBody @Valid EmailSignUpVo vo) {

        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), userModifyProviderService.createLoginProviderByEmail(currentUser, vo.getEmail(), currentUser.getNickname(), vo.getPassword(), UserProfileProviderType.email), CommonResponseCode.SUCCESS.getResultMessage());
    }

	@PostMapping(path = "/providers/mail")
	@ApiOperation(value = "계정연동 - 기존 휴대폰 로그인 사용자 - email 인증 번호 확인 및 전환")
	public CommonResponse<UserModifyProviderLoginDto> doUpdateLoginProviderByEmail(@ApiIgnore User currentUser, @Valid @RequestBody MyProfileEmailVo vo) {

		return new CommonResponse<>(CommonResponseCode.SUCCESS, userModifyProviderService.updateLoginProviderByEmail(currentUser, vo));
	}

}
