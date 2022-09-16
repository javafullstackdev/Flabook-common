package com.msquare.flabook.form.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class EmailLoginVo{
	@NotBlank(message = "이메일 주소를 입력해 주세요.")
    @Email(message = "메일의 양식을 지켜주세요.")
	String email;

	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
	String password;

}
