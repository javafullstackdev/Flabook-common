package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.UserWithExpertGroupDto;
import com.msquare.flabook.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserWithExpertGroupMapper {
    UserWithExpertGroupMapper INSTANCE = Mappers.getMapper( UserWithExpertGroupMapper.class );
    UserWithExpertGroupDto of(User user);
}
