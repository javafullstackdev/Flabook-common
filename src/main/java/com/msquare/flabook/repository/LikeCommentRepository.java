package com.msquare.flabook.repository;

import com.msquare.flabook.models.LikeComment;
import com.msquare.flabook.models.UserCommentRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.util.List;

public interface LikeCommentRepository extends JpaRepository<LikeComment, Long> {

    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from LikeComment l where l.relation = :relation ")
    LikeComment findByIdEquals(@Param("relation") UserCommentRelation relation);

    @Query("select l.relation.comment.id as id, l.relation.user.id as uId from LikeComment l where l.relation.comment.id in :ids and l.relation.user.id = :userId")
    List<Tuple> findUserLikes(@Param("ids") List<Long> ids, @Param("userId")  long userId);

}
