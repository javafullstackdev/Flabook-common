package com.msquare.flabook.repository;

import com.msquare.flabook.models.KeywordRanking;
import com.msquare.flabook.models.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface KeywordRankingRepository extends JpaRepository<KeywordRanking, Long> {

    List<KeywordRanking> findAllByRankingTypeAndDate(Ranking.RankingType rankingType, ZonedDateTime date);
}
