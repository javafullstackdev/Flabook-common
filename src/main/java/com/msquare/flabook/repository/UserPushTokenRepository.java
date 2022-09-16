package com.msquare.flabook.repository;

import com.msquare.flabook.models.UserPushToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserPushTokenRepository extends JpaRepository<UserPushToken, Long> {

    List<UserPushToken> findAllByUserId(Long userInfoId);
    Optional<UserPushToken> findByUserIdAndOsType(Long userInfoId, String osType);
    Optional<UserPushToken> findByUserIdAndDeviceId(Long userInfoId, String deviceId);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserPushToken upt where upt.user.id = :user_id")
    void deleteByUserId(@Param("user_id") long userId);


    @Transactional
    @Modifying
    @Query("DELETE FROM UserPushToken upt where upt.user.id = :user_id AND token <> :token AND upt.osType <> :os_type")
    void deleteDuplicateToken(@Param("user_id") long userId, @Param("token") String token, @Param("os_type") String osType);
}
