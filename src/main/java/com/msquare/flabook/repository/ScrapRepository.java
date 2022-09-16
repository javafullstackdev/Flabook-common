package com.msquare.flabook.repository;

import com.msquare.flabook.models.ScrapPosting;
import com.msquare.flabook.models.UserPostingRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.util.List;

public interface ScrapRepository extends JpaRepository<ScrapPosting, UserPostingRelation> {

    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from ScrapPosting l where l.relation = :relation ")
    ScrapPosting findByIdEquals(@Param("relation") UserPostingRelation relation);

    /**
     * 게시글ID 목록으로 해당 사용자의 스크랩 목록 조회
     *
     * @param ids 게시글 id 목록
     * @param userId 사용자 id
     * @return 스크랩 entity 와 게시글 id
     */
    @Query("select l.relation.posting.id as id, l.relation.user.id as uId from ScrapPosting l where l.relation.posting.id in :ids and l.relation.user.id = :userId")
    List<Tuple> findScraps(@Param("ids") List<Long> ids, @Param("userId") long userId);
}
