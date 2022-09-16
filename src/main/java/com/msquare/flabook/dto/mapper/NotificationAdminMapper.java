package com.msquare.flabook.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.msquare.flabook.dto.NotificationAdminDto;
import com.msquare.flabook.models.NotificationAdmin;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationAdminMapper {
    NotificationAdminMapper INSTANCE = Mappers.getMapper( NotificationAdminMapper.class );
    NotificationAdminDto of(NotificationAdmin notification);
}
