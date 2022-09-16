package com.msquare.flabook.repository;


import com.msquare.flabook.models.GambleResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.util.List;

public interface GambleResultRepository extends JpaRepository<GambleResult, Long> {


    @Query("select max(gr.createdAt), count(gr) from GambleResult gr where gr.gamble.id = :gambleId and gr.user.id = :userId and gr.version = :version")
    Tuple findGambleDateWithCount(@Param("gambleId") Long gambleId, @Param("userId") Long userId, @Param("version") int version);

    @Query("select gr from GambleResult gr where gr.gamble.id = :gambleId and gr.user.id = :userId and gr.version = :version")
    List<GambleResult> findGambleResult(@Param("gambleId") Long gambleId, @Param("userId") Long userId, @Param("version") int version);

    @Query("select count(gr) from GambleResult gr where gr.gamble.id = :gambleId and gr.version = :version")
    long findGambleResultCount(@Param("gambleId") Long gambleId, @Param("version") int version);

    @Query("select count(gr) from GambleResult gr where gr.gamble.id = :gambleId")
    long findGambleResultCount(@Param("gambleId") Long gambleId);

}
