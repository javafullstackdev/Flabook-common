package com.msquare.flabook.repository;

import com.msquare.flabook.models.LikePosting;
import com.msquare.flabook.models.UserPostingRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.util.List;

public interface LikePostingRepository extends JpaRepository<LikePosting, Long> {

    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from LikePosting l where l.relation = :relation ")
    LikePosting findByIdEquals(@Param("relation") UserPostingRelation relation);

    @Query("select l.relation.posting.id as id, l.relation.user.id as uId from LikePosting l where l.relation.posting.id in :ids and l.relation.user.id = :userId")
    List<Tuple> findLikes(@Param("ids") List<Long> ids, @Param("userId") long userId);
}
