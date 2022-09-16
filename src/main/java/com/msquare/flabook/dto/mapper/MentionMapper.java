package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.CommentDto;
import com.msquare.flabook.dto.MentionDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MentionMapper {
    MentionMapper INSTANCE = Mappers.getMapper( MentionMapper.class );
    MentionDto of(CommentDto comment);
}
