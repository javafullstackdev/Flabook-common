package com.msquare.flabook.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.msquare.flabook.dto.TagAdminDto;
import com.msquare.flabook.models.Tag;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagAdminMapper {
    TagAdminMapper INSTANCE = Mappers.getMapper( TagAdminMapper.class );
    TagAdminDto of(Tag tag);
}
