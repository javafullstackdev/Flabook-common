package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.BannerDto;
import com.msquare.flabook.models.Banner;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BannerMapper {
    BannerMapper INSTANCE = Mappers.getMapper( BannerMapper.class );
    BannerDto of(Banner banner);
}
