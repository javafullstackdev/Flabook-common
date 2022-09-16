package com.msquare.flabook.form.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@SuppressWarnings("java:S1104")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressVo {

    @NotBlank(message = "도로명 주소를 입력해주세요")
    public String roadAddress;

    @Pattern(regexp = "[0-9]{5}", message = "우편번호 형식이 올바르지 않습니다.(숫자 5자리)")
    @NotBlank(message = "우편번호를 입력해주세요")
    public String postCode;

    @NotBlank(message = "상세주소를 입력해주세요")
    public String detailAddress;

    @NotBlank(message = "이름을 입력해주세요.")
    public String name;

    @NotBlank(message = "전화번호를 입력해주세요.")
    public String phone1;

    public String phone2;
}
