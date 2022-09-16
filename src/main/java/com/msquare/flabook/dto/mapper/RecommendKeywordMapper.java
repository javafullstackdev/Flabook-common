package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.RecommendKeywordDto;
import com.msquare.flabook.models.RecommendKeyword;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendKeywordMapper {
    RecommendKeywordMapper INSTANCE = Mappers.getMapper(RecommendKeywordMapper.class);
    RecommendKeywordDto of(RecommendKeyword recommendKeyword);
}
