package com.msquare.flabook.repository;

import com.msquare.flabook.models.AbstractCommentActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CommentActivityRepository<T extends AbstractCommentActivity> extends JpaRepository<T, Long> {

}
