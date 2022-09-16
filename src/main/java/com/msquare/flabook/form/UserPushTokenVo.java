package com.msquare.flabook.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class UserPushTokenVo {

	@ApiModelProperty(value = "사용자 PUSH 토큰")
	@NotBlank(message = "사용자 PUSH 토큰을 입력해 주세요.")
	private String token;

	@ApiModelProperty(value = "사용자 기기 정보")
	@NotBlank(message = "사용자 기기정보를 입력해 주세요.")
	private String deviceId;

	@ApiModelProperty(value = "사용자 기기 OS 정보")
	private String osType = "ios";
}
