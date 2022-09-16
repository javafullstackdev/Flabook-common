package com.msquare.flabook.repository;

import com.msquare.flabook.models.Badge;
import com.msquare.flabook.models.User;
import com.msquare.flabook.models.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    @Query("select ub from UserBadge ub where ub.user=:currentUser and ub.badge = :badge")
    Optional<UserBadge> findUserBadge(@Param("currentUser") User currentUser,@Param("badge") Badge badge);
}
