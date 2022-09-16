package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.ShippingDto;
import com.msquare.flabook.models.UserEventInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper( AddressMapper.class );

    ShippingDto of(UserEventInfo userEventInfo);
}
