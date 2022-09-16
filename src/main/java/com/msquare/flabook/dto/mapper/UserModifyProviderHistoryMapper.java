package com.msquare.flabook.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.msquare.flabook.dto.UserModifyProviderHistoryDto;
import com.msquare.flabook.models.UserModifyProviderHistory;

@Mapper
public interface UserModifyProviderHistoryMapper {

    UserModifyProviderHistoryMapper INSTANCE = Mappers.getMapper( UserModifyProviderHistoryMapper.class );

    UserModifyProviderHistoryDto of(UserModifyProviderHistory userModifyProviderHistory);
}
