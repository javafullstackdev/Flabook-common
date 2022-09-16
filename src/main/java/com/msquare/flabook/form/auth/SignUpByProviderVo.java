package com.msquare.flabook.form.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class SignUpByProviderVo{
//	@NotBlank
//	@Pattern(message = ValidationPattern.PATTERN_NICKNAME_MESSAGE, regexp = ValidationPattern.PATTERN_NICKNAME_REGEXP)
	String nickName;
	@NotBlank
	String accessToken;
}
