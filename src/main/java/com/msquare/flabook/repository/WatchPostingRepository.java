package com.msquare.flabook.repository;

import com.msquare.flabook.models.UserPostingRelation;
import com.msquare.flabook.models.WatchPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.util.List;

public interface WatchPostingRepository extends JpaRepository<WatchPosting, UserPostingRelation> {

    @Query("select w from WatchPosting w where w.relation.posting.id = :id")
    public List<WatchPosting> findWatchesByPostingId( @Param("id")long id);

    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from WatchPosting l where l.relation = :relation ")
    WatchPosting findByIdEquals(@Param("relation") UserPostingRelation relation);

    @Query("select l.relation.posting.id as id, l.relation.user.id as uId from WatchPosting l where l.relation.posting.id in :ids and l.relation.user.id = :userId")
    List<Tuple> findWatches(@Param("ids") List<Long> ids, @Param("userId") long userId);
}
