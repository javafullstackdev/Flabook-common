package com.msquare.flabook.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.msquare.flabook.dto.ReportPostingDto;
import com.msquare.flabook.models.ReportPosting;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReportPostingMapper {
	ReportPostingMapper INSTANCE = Mappers.getMapper( ReportPostingMapper.class );
    ReportPostingDto of(ReportPosting reportPosting);
}
