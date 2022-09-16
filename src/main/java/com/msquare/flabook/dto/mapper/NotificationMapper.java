package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.NotificationDto;
import com.msquare.flabook.models.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper( NotificationMapper.class );
    NotificationDto of(Notification notification);
}
