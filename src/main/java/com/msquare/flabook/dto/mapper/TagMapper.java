package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.TagDto;
import com.msquare.flabook.models.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {
    TagMapper INSTANCE = Mappers.getMapper( TagMapper.class );
    TagDto of(Tag tag);
}
