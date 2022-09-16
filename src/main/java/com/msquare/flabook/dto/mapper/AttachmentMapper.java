package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.AttachmentDto;
import com.msquare.flabook.models.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttachmentMapper {
    AttachmentMapper INSTANCE = Mappers.getMapper( AttachmentMapper.class );
    AttachmentDto of(Attachment attachment);
}
