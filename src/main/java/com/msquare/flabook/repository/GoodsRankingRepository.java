package com.msquare.flabook.repository;

import com.msquare.flabook.models.GoodsRanking;
import com.msquare.flabook.models.Ranking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface GoodsRankingRepository extends JpaRepository<GoodsRanking, Long> {
    @Query("select g from GoodsRanking  g where g.rankingType = :rankingType and g.active = true and ((g.start is null and g.end is null) or (g.start >= :date and g.end is null) or (g.start is null and g.end < :date) or (g.start <= :date and :date < g.end))")
    List<GoodsRanking> findList(@Param("rankingType") Ranking.RankingType rankingType, @Param("date") ZonedDateTime date);

    @Query("select g from GoodsRanking g where g.rankingType = :rankingType")
    List<GoodsRanking> findAllByRankTypeWithPageable(@Param("rankingType") Ranking.RankingType rankingType, Pageable pageable);

    @Query("select g from GoodsRanking g where g.rankingType = :rankingType")
    List<GoodsRanking> findAllByRankType(@Param("rankingType") Ranking.RankingType rankingType);
}
