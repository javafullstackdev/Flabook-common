package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.RecommendKeywordAdminDto;
import com.msquare.flabook.dto.RecommendKeywordDto;
import com.msquare.flabook.models.RecommendKeywordAdmin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface RecommendKeywordAdminMapper {

    RecommendKeywordAdminMapper INSTANCE = Mappers.getMapper(RecommendKeywordAdminMapper.class);

    @Mapping(target = "keywords", source = "recommendKeywordAdmin", qualifiedByName = "recommendKeywords")
    RecommendKeywordAdminDto of(RecommendKeywordAdmin recommendKeywordAdmin);

    @Named("recommendKeywords")
    default List<RecommendKeywordDto> recommendKeywords(RecommendKeywordAdmin recommendKeywordAdmin){
        return recommendKeywordAdmin.getKeywords().stream().map(RecommendKeywordMapper.INSTANCE::of).collect(Collectors.toList());
    }
}
