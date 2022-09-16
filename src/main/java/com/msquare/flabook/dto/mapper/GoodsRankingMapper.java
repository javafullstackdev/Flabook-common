package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.GoodsRankingDto;
import com.msquare.flabook.models.GoodsRanking;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GoodsRankingMapper {

    GoodsRankingMapper INSTANCE = Mappers.getMapper(GoodsRankingMapper.class);

    GoodsRankingDto of(GoodsRanking goodsRanking);
}
