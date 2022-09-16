package com.msquare.flabook.form.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class PasswordFindModifyVo{
	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(message = ValidationPattern.PATTERN_PASSWORD_MESSAGE, regexp = ValidationPattern.PATTERN_PASSWORD_REGEXP)
	String password;

	String authKey;
}
