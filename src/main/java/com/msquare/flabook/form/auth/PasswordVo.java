package com.msquare.flabook.form.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class PasswordVo{
	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(message = ValidationPattern.PATTERN_PASSWORD_MESSAGE, regexp = ValidationPattern.PATTERN_PASSWORD_REGEXP)
	String password;
}
