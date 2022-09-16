package com.msquare.flabook.repository;

import com.msquare.flabook.models.AdoptComment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@SuppressWarnings("unused")
public interface AdoptCommentRepository extends CommentActivityRepository<AdoptComment> {

    /**
     * 채택된 답변이 있는지 조회
     *
     * ===== 주의 =====
     * UK_ADOPT unique index 는 user_id, posting_id 를 쌍으로 가지므로 posting_id 로 검색시 인덱스를 타지 않음
     * comment 의 댓글 작성자를 기준으로 user_id 를 가져와 인덱스를 탈 수 있도록 subquery 를 하도록 추가함
     *
     * ===== 개선 필요 =====
     * TODO limit 제한을 1로 주면 성능이 개선되나 PageRequest 인자를 외부에서 추가해야함 (어떻게 할까?)
     *
     * @param postingId 게시글Id
     * @return 존재여부 boolean
     */
    @Query("select (count(c) > 0) as b from AdoptComment c where c.relation.user.id in (select distinct c2.owner.id from Comment c2 where c2.posting.id = :postingId) and c.relation.posting.id = :postingId")
    boolean existsByPostingId(@Param("postingId") long postingId);
}
