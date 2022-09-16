package com.msquare.flabook.repository;

import com.msquare.flabook.dto.SearchQueryCountDto;
import com.msquare.flabook.models.SearchQueryHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface SearchQueryHistoryRepository extends JpaRepository<SearchQueryHistory, Long> {

    @Query("select sqh from SearchQueryHistory sqh where sqh.dt = :dt and sqh.keyword = :keyword")
    Optional<SearchQueryHistory> find(@Param("dt") ZonedDateTime dt, @Param("keyword")  String keyword);

    @Query("select new com.msquare.flabook.dto.SearchQueryCountDto(sqh.keyword , sum(sqh.count) as cnt) from SearchQueryHistory sqh where :from <= sqh.dt and sqh.dt <= :to group by sqh.keyword order by cnt desc")
    List<SearchQueryCountDto> findGroupListByRange(@Param("from") ZonedDateTime from, @Param("to") ZonedDateTime to, Pageable pageable);

}
