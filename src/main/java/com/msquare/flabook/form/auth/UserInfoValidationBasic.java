package com.msquare.flabook.form.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoValidationBasic {
	Long userInfoId;
	int authKey;

	@NotBlank(message = "이메일 주소를 입력해 주세요.",
			groups= {UserInfoValidation.EmailLogIn.class,
					UserInfoValidation.EmailSignUp.class,
					UserInfoValidation.Email.class})
    @Email(message = "메일의 양식을 지켜주세요.",
    		groups= {UserInfoValidation.EmailLogIn.class,
    				UserInfoValidation.EmailSignUp.class,
    				UserInfoValidation.Email.class})
	String email;

	@Pattern(message = ValidationPattern.PATTERN_NICKNAME_MESSAGE, regexp = ValidationPattern.PATTERN_NICKNAME_REGEXP,
			groups= {UserInfoValidation.EmailSignUp.class,
					UserInfoValidation.nickName.class})
	String nickName;

	@NotBlank(message = "비밀번호는 필수 입력 값입니다.",
			groups= {UserInfoValidation.EmailLogIn.class,
					UserInfoValidation.EmailSignUp.class,
					UserInfoValidation.password.class})
    @Pattern(message = ValidationPattern.PATTERN_PASSWORD_MESSAGE, regexp = ValidationPattern.PATTERN_PASSWORD_REGEXP,
    		groups= {UserInfoValidation.EmailSignUp.class,
    				UserInfoValidation.password.class})
	String password;

	String provider;
	String accessToken;
}
