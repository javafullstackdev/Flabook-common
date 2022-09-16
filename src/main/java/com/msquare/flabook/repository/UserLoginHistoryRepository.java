package com.msquare.flabook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.msquare.flabook.models.UserLoginHistory;

public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, Long> {

}
