package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.UserIdWithNicknameDto;
import com.msquare.flabook.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserIdWithNicknameMapper {
    UserIdWithNicknameMapper INSTANCE = Mappers.getMapper( UserIdWithNicknameMapper.class );
    UserIdWithNicknameDto of(User user);
}
