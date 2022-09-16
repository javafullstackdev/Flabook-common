package com.msquare.flabook.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.msquare.flabook.models.UserExpertGroup;

public interface UserExpertGroupRepository extends JpaRepository<UserExpertGroup, Long> {
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "delete from user_expert_group where user_id = :userId")
	void deleteAllByUserId(@Param("userId") long userId);
}
