package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.UserPostingRelationDto;
import com.msquare.flabook.models.UserPostingRelation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserPostingRelationMapper {
	UserPostingRelationMapper INSTANCE = Mappers.getMapper( UserPostingRelationMapper.class );
	UserPostingRelationDto of(UserPostingRelation userRelation);
}
