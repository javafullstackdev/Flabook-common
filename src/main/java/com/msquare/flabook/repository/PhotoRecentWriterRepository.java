package com.msquare.flabook.repository;

import com.msquare.flabook.models.PhotoRecentWriter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PhotoRecentWriterRepository extends JpaRepository<PhotoRecentWriter, Long> {

    @Query(value = "select * from photo_recent_writer prw where prw.user_id = :id", nativeQuery = true)
    Optional<PhotoRecentWriter> findIdByUserId(@Param("id") Long id);

    @Query(value = "select * from photo_recent_writer prw where prw.posting_id = :id", nativeQuery = true)
    Optional<PhotoRecentWriter> findIdByPostingId(@Param("id") Long postingId);
}
