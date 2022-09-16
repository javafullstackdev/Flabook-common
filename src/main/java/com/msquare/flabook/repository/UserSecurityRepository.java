package com.msquare.flabook.repository;

import com.msquare.flabook.models.UserSecurity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.util.Optional;

public interface UserSecurityRepository extends JpaRepository<UserSecurity, Long> {
    public Optional<UserSecurity> findByAccessToken(String accessToken);

    @Query(value = "select us.id, us.accessTokenExpireAt from UserSecurity us where us.accessToken = :accessToken")
    public Optional<Tuple> findIdWithAccessTokenExpireAtByAccessToken(@Param("accessToken") String accessToken);
}
