package com.msquare.flabook.repository;

import com.msquare.flabook.models.RankingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RankingOrderRepository extends JpaRepository<RankingOrder, Long> {

    @Query("select ro from RankingOrder  ro where ro.rankingGenre = :genre")
    Optional<RankingOrder> findByRankingGenre(@Param("genre") String genre);
}
