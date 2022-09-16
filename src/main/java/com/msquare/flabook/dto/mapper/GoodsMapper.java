package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.GoodsDto;
import com.msquare.flabook.dto.shop.GoodsDataDto;
import com.msquare.flabook.models.shop.Goods;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoodsMapper {
    GoodsMapper INSTANCE = Mappers.getMapper( GoodsMapper.class );
    Goods of(GoodsDataDto goodsDataDto);
    GoodsDto of(Goods goods);
}
