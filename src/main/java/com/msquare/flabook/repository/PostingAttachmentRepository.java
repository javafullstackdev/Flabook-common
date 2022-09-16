package com.msquare.flabook.repository;

import com.msquare.flabook.models.PostingAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface PostingAttachmentRepository extends JpaRepository<PostingAttachment, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from PostingAttachment c where c.id = :id and c.dtype = 'P'")
    Optional<PostingAttachment> findLockOnly(@Param("id") long id);

}
