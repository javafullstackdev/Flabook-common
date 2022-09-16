package com.msquare.flabook.repository;

import com.msquare.flabook.models.NoticePrivate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticePrivateRepository extends JpaRepository<NoticePrivate, Long> {
	Optional<NoticePrivate> findByIsDelete(Boolean deleted);

	List<NoticePrivate> findAllByIsDelete(Boolean deleted);

	List<NoticePrivate> findAllByIsDeleteOrderByPositionAsc(Boolean deleted);
}
