package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.CommentDto;
import com.msquare.flabook.models.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper( CommentMapper.class );
    CommentDto of(Comment comment);
}
