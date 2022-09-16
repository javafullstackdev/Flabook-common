package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.ShopUserDto;
import com.msquare.flabook.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShopUserMapper {

    ShopUserMapper INSTANCE = Mappers.getMapper( ShopUserMapper.class );
    ShopUserDto of(User user);
}
