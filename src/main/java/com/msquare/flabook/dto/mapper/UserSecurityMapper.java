package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.UserSecurityDto;
import com.msquare.flabook.models.UserSecurity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserSecurityMapper {
    UserSecurityMapper INSTANCE = Mappers.getMapper( UserSecurityMapper.class );
    UserSecurityDto of(UserSecurity security);
}
