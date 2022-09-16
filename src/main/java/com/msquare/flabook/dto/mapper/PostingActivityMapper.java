package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.PostingActivityDto;
import com.msquare.flabook.models.Posting;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostingActivityMapper {
    PostingActivityMapper INSTANCE = Mappers.getMapper( PostingActivityMapper.class );
    PostingActivityDto of(Posting posting);
}
