package com.msquare.flabook.repository;

import com.msquare.flabook.models.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    /**
     * lastDate 이전 날짜 통계중 마지막 하나
     * @param lastDate 기준날짜
     * @return StatisticsDaily
     */
    @Query(nativeQuery = true, value = "select * from statistics s where dt > :lastDate order by dt desc limit 1")
    public Statistics findLastOnce(@Param("lastDate") ZonedDateTime lastDate);

    @Query(value="select s from Statistics s where dt = :date")
    public Statistics findByDate(@Param("date") ZonedDateTime date);


    @Query(value="select s from Statistics s where dt between :from and :to")
    public List<Statistics> findByRange(@Param("from") ZonedDateTime from, @Param("to") ZonedDateTime to);

}
