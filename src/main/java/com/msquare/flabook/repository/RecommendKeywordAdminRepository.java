package com.msquare.flabook.repository;

import com.msquare.flabook.enumeration.RecommendKeywordType;
import com.msquare.flabook.models.RecommendKeywordAdmin;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecommendKeywordAdminRepository extends JpaRepository<RecommendKeywordAdmin, Long> {

    @Query("select rca from RecommendKeywordAdmin rca where :query in (select k.name from rca.keywords k)")
    List<RecommendKeywordAdmin> findBySearchQuery(@Param("query") String query);

    @Query("select rca from RecommendKeywordAdmin rca where :query in (select k.name from rca.keywords k)")
    List<RecommendKeywordAdmin> findBySearchQuery(@Param("query") String query, Pageable pageable);

    @Query("select rca from RecommendKeywordAdmin rca where rca.type = :searchType")
    List<RecommendKeywordAdmin> findBySearchType(@Param("searchType") RecommendKeywordType searchType);

    @Query("select rca from RecommendKeywordAdmin rca where rca.type = :searchType")
    List<RecommendKeywordAdmin> findBySearchType(@Param("searchType") RecommendKeywordType searchType, Pageable pageable);

    @Query("select rca from RecommendKeywordAdmin rca where :query in (select k.name from rca.keywords k) and rca.type = :searchType")
    List<RecommendKeywordAdmin> findBySearchQueryAndSearchType(@Param("searchType") RecommendKeywordType searchType, @Param("query") String query);

    @Query("select rca from RecommendKeywordAdmin rca where :query in (select k.name from rca.keywords k) and rca.type = :searchType")
    List<RecommendKeywordAdmin> findBySearchQueryAndSearchType(@Param("searchType") RecommendKeywordType searchType, @Param("query") String query, Pageable pageable);


    @Query(value = "select rk.name from recommend_keyword rk where rk.recommendkeywordadmin_id = (select rca.id from recommend_keyword_admin rca where rca.type=:type order by rca.created_at desc limit 1)", nativeQuery = true)
    List<String> findRecentlyKeyword(@Param("type") String recommendKeywordType); //, Pageable pageable

    @Query(value = "select rk.name from recommend_keyword rk where rk.recommendkeywordadmin_id in (select max(id) from recommend_keyword_admin rca group by rca.type) ", nativeQuery = true)
    List<String> findRecentlyKeyword(); //, Pageable pageable

}
