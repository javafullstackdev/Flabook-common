package com.msquare.flabook.repository;

import com.msquare.flabook.models.User;
import com.msquare.flabook.models.UserEventInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserEventInfoRepository extends JpaRepository<UserEventInfo, Long> {

    @Query("select ue from UserEventInfo ue where ue.user = :user")
    Optional<UserEventInfo> findByUser(@Param("user")User user);
}
