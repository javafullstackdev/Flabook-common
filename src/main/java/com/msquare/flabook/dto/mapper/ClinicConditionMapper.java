package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.ClinicConditionDto;
import com.msquare.flabook.models.board.ClinicCondition;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClinicConditionMapper {
    ClinicConditionMapper INSTANCE = Mappers.getMapper( ClinicConditionMapper.class );

    ClinicConditionDto of(ClinicCondition clinicCondition);
}
