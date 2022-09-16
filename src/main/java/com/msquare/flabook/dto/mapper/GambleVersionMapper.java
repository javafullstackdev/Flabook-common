package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.GambleVersionDto;
import com.msquare.flabook.models.GambleVersion;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GambleVersionMapper {
    GambleVersionMapper INSTANCE = Mappers.getMapper( GambleVersionMapper.class );
    GambleVersionDto of(GambleVersion gambleVersion);
}
