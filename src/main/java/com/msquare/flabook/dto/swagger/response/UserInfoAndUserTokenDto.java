package com.msquare.flabook.dto.swagger.response;

import java.io.Serializable;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.UserDto;
import com.msquare.flabook.dto.mapper.UserWithSecurityMapper;
import com.msquare.flabook.util.CommonUtils;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoAndUserTokenDto implements Serializable{
	private UserDto userInfo;
	private String userToken;
	private String userTokenExpireAt;
	private String refreshToken;
	private String refreshTokenExpireAt;

	@JsonInclude(JsonInclude.Include.ALWAYS)
	private Boolean adNotiAgreement;
	private ZonedDateTime adNotiConfirmedAt;

	public static UserInfoAndUserTokenDto of(UserDto userInfo, String userToken, ZonedDateTime accessTokenExpireAt, String refreshToken, ZonedDateTime refreshTokenExpireAt, Boolean isAdNotiAgreement, ZonedDateTime adNotiConfirmedAti) {
        return UserWithSecurityMapper.INSTANCE.of(userInfo, userToken, CommonUtils.convertDateToString(accessTokenExpireAt), refreshToken, CommonUtils.convertDateToString(refreshTokenExpireAt), isAdNotiAgreement, adNotiConfirmedAti);
    }
}
