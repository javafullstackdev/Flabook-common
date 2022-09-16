package com.msquare.flabook.form.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class UseableVo{
	@Pattern(message = ValidationPattern.PATTERN_NICKNAME_MESSAGE, regexp = ValidationPattern.PATTERN_NICKNAME_REGEXP)
	String nickName;

	@Email(message = "메일의 양식을 지켜주세요.")
	String email;
}
