package com.msquare.flabook.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.msquare.flabook.dto.UserDto;
import com.msquare.flabook.dto.UserWithSecurityDto;
import com.msquare.flabook.dto.swagger.response.UserInfoAndUserTokenDto;
import com.msquare.flabook.models.User;

import java.time.ZonedDateTime;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserWithSecurityMapper {
    UserWithSecurityMapper INSTANCE = Mappers.getMapper( UserWithSecurityMapper.class );
    UserWithSecurityDto of(User user);

    @Mapping(target = "photoEnable", source = "user", qualifiedByName = "setPhotoEnable")
    UserWithSecurityDto ofWithPhotoProperty(User user);

    @Named("setPhotoEnable")
    default boolean setPhotoEnable(User user){
        return user.getUserSetting().getPhotoEnable();
    }


    UserInfoAndUserTokenDto of(UserDto userInfo, String userToken, String userTokenExpireAt, String refreshToken, String refreshTokenExpireAt, Boolean isAdNotiAgreement, ZonedDateTime adNotiAgreedAt);
}
