package com.msquare.flabook.repository;

import com.msquare.flabook.models.Gamble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface GambleRepository extends JpaRepository<Gamble, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select gb from Gamble gb where gb.id = :id")
    Optional<Gamble> findByIdLockOnly(@Param("id") Long id);
}
