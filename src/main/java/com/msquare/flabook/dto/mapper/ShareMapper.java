package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.ShareDto;
import com.msquare.flabook.models.Share;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShareMapper {

    ShareMapper INSTANCE = Mappers.getMapper( ShareMapper.class );
    ShareDto of(Share share);
}
