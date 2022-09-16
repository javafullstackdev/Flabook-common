package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.UserSettingDto;
import com.msquare.flabook.models.UserSetting;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserSettingMapper {
    UserSettingMapper INSTANCE = Mappers.getMapper( UserSettingMapper.class );
    UserSettingDto of(UserSetting user);
}
