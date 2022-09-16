package com.msquare.flabook.repository;

import com.msquare.flabook.models.RankingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface RankingLogRepository extends JpaRepository<RankingLog, Long> {

    Optional<RankingLog> findByDate(ZonedDateTime date);

    @Query(nativeQuery = true, value = "select l.* from ranking_log l where l.date < :date order by date desc limit 1")
    Optional<RankingLog> findBeforeByDate(@Param("date") ZonedDateTime date);

    @Query(nativeQuery = true, value = "select l.* from ranking_log l where l.date <= :date order by date desc limit 1")
    Optional<RankingLog> findLastByDate(@Param("date") ZonedDateTime date);

}
