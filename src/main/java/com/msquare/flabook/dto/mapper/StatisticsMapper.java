package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.StatisticsDto;
import com.msquare.flabook.models.Statistics;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StatisticsMapper {

    StatisticsMapper INSTANCE = Mappers.getMapper( StatisticsMapper.class );
    StatisticsDto of(Statistics statistics);
}
