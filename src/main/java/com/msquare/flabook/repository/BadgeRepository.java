package com.msquare.flabook.repository;

import com.msquare.flabook.models.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    @Query("select b from Badge b where b.title like :badgeName ")
    Optional<Badge> findByName(@Param("badgeName") String badgeName);

    @Query("select b from Badge b where b.active=true order by b.orderCount asc")
    List<Badge> findBadgesByActiveTrue();
}
