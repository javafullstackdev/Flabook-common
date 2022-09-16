package com.msquare.flabook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.mapper.GoodsRankingMapper;
import com.msquare.flabook.models.GoodsRanking;
import com.msquare.flabook.models.Ranking;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsRankingDto {
    private Long id;
    private List<GoodsRanking.GoodsRankingDetail> items;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private Ranking.RankingType rankingType;
    private boolean active;

    public static GoodsRankingDto of(GoodsRanking goodsRanking) {
        return GoodsRankingMapper.INSTANCE.of(goodsRanking);
    }
}
