package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.GambleItemDto;
import com.msquare.flabook.models.GambleItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GambleItemMapper {
    GambleItemMapper INSTANCE = Mappers.getMapper( GambleItemMapper.class );
    GambleItemDto of(GambleItem gambleItem);
}
