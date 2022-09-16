package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.GambleDto;
import com.msquare.flabook.models.Gamble;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GambleMapper {
    GambleMapper INSTANCE = Mappers.getMapper( GambleMapper.class );
    GambleDto of(Gamble gamble);
}
