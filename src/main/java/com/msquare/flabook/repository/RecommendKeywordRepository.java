package com.msquare.flabook.repository;

import com.msquare.flabook.enumeration.RecommendKeywordType;
import com.msquare.flabook.models.RecommendKeyword;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecommendKeywordRepository extends JpaRepository<RecommendKeyword, Long> {

    @Query(value = "select k.name from RecommendKeyword k where k.type = :type")
    List<String> findList(@Param("type") RecommendKeywordType type, Pageable pageable);


    @Query(value = "select k.name from RecommendKeyword k group by k.name order by sum(k.weight) desc")
    List<String> findList(Pageable pageable);
}
