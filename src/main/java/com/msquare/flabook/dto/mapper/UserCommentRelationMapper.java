package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.UserCommentRelationDto;
import com.msquare.flabook.models.UserCommentRelation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserCommentRelationMapper {
	UserCommentRelationMapper INSTANCE = Mappers.getMapper( UserCommentRelationMapper.class );
	UserCommentRelationDto of(UserCommentRelation userRelation);
}
