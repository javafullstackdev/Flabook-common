package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.ShippingDto;
import com.msquare.flabook.dto.GambleResultDto;
import com.msquare.flabook.models.GambleResult;
import com.msquare.flabook.models.UserEventInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GambleResultMapper {
    GambleResultMapper INSTANCE = Mappers.getMapper( GambleResultMapper.class );


    @Named("addressOf")
    static ShippingDto addressOf(UserEventInfo userEventInfo) {
        return ShippingDto.of(userEventInfo);
    }

    @Mapping(target = "address", source = "user.eventInfo", qualifiedByName = "addressOf")
    GambleResultDto of(GambleResult gambleResult);
}
