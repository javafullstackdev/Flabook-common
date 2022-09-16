package com.msquare.flabook.repository;

import com.msquare.flabook.models.Comment;
import com.msquare.flabook.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import javax.persistence.Tuple;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Comment c where c.id = :id")
    Optional<Comment> findLockOnly(@Param("id")long id);

    @SuppressWarnings("unused")
    Optional<Comment> findFirstByPostingId(long id);

    @Query("select c  from Comment c where c.posting.id = :id and c.isAdopt = true")
    Optional<Comment> findAdoptedByPostingId(@Param("id") long id);

    @Query("select c from Comment c where c.posting.id = :id")
    List<Comment> findListByPostingId(@Param("id") long id, Pageable pageable);

    @SuppressWarnings("UnusedReturnValue")
    @Modifying
    @Query(value = "UPDATE Comment c set c.likeCount = c.likeCount + :sum where c.id = :id")
    int updateLikeCount(@Param("id")long id, @Param("sum") int sum);

    @SuppressWarnings("unused")
    @Modifying
    @Query(value = "UPDATE Comment c set c.reportCount = c.reportCount + :sum where c.id = :id")
    int updateReportCount(@Param("id")long id, @Param("sum") int sum);


    //@Query(nativeQuery = true, value = "select count(c.id) from comment c where c.posting_id = :id and c.owner_id = :ownerId and c.is_delete = false")
    @Query("select count(c) from Comment c where c.posting.id = :id and c.owner.id = :ownerId and c.isDelete=false")
    Integer countByPostingIdAndOwnerId(@Param("id")long id, @Param("ownerId") long ownerId);

    /**
     * 게시글에 답변한 사용자 리스트(대댓글 제외)
     * @param postingId 게시글에Id
     * @return List<User> 답변 사용자 목록 대댓글 제외
     */
   // @Query(nativeQuery = true, value= "select distinct u.* from comment c join user u on c.owner_id = u.id  where c.posting_id = :postingId and c.parent_id is null")
    @Query("select distinct c.owner from Comment c where c.posting.id = :postingId and c.parent is null and c.isDelete = false")
    List<User> findDistinctRecipientWithoutReplyList(@Param("postingId") long postingId);

    /**
     * 게시글에 답변한 사용자 리스트
     * @param postingId 게시글에Id
     * @return List<User> 답변 사용자 목록 대댓글 포함
     */
    // @Query(nativeQuery = true, value= "select distinct u.* from comment c join user u on c.owner_id = u.id  where c.posting_id = :postingId and c.parent_id is null")
    @Query("select distinct c.owner from Comment c where c.owner.nickname like %:query% and c.posting.id = :postingId and c.isDelete = false")
    List<User> findDistinctRecipientList(@Param("postingId") long postingId, @Param("query") String query, Sort sort);

    /**
     * 게시글에 답변한 사용자 리스트
     * @param postingId 게시글에Id
     * @return List<User> 답변 사용자 목록 대댓글 포함
     */
    @Query("select distinct c.owner from Comment c where c.posting.id = :postingId and c.isDelete = false")
    List<User> findDistinctRecipientList(@Param("postingId") long postingId);

    /**
     * 게시글에 참여하고 참여 댓글알림을 모두 받도록 설정된 사용자 리스트
     * @param postingId 게시글ID
     * @return List<User> 알림 설정 자용자 목록
     */
    @Query("select distinct o from Comment c join c.owner o join o.userSetting s where c.posting.id = :postingId and c.isDelete = false and s.joinCommentNotiEnable = true")
    List<User> findDistinctJoinPushEnabledRecipientList(@Param("postingId") long postingId);

    /**
     * 게시글에 답변한 작성자 중 언급된 사용자 id 가 포함된 사용자 리스트
     * @param postingId 게시글ID
     * @param mentionUserIds 언급된 사용자 ID
     * @return List<User> 언급된 사용자 목록
     */
    @Query("select distinct c.owner from Comment c where c.posting.id = :postingId and c.owner.id in (:mentionUserIds) and c.isDelete = false")
    List<User> findDistinctRecipientByMentionUserIds(@Param("postingId") long postingId, @Param("mentionUserIds") List<Long> mentionUserIds);

    @Query(nativeQuery = true, value="select c.text as text, u.nickname as nickname, c.posting_id as postingId, u.level as level, u.id as uid from  (\n" +
            "        select min(c.id) id from comment c\n" +
            "        where c.posting_id in (:postingIds) and c.is_delete = false and c.is_blind = false and c.text  RLIKE '#[a-z|A-Z|가-힣|_|]+'\n" +
            "        group by c.posting_id\n" +
            "    ) cc join comment c on c.id = cc.id\n" +
            "    join user u on u.id = c.owner_id")
    List<Tuple> findFirstHashtags(@Param("postingIds") List<Long> postingIds);

    @Query(nativeQuery = true, value="select c.id from  (\n" +
            "        select min(c.id) id from comment c\n" +
            "        where c.posting_id in (:postingIds) and c.is_delete = false and c.is_blind = false and c.text  RLIKE '#[a-z|A-Z|가-힣|_|]+'\n" +
            "        group by c.posting_id\n" +
            "    ) cc join comment c on c.id = cc.id\n" +
            "    join user u on u.id = c.owner_id")
    List<BigInteger> findFirstHashtagIds(@Param("postingIds") List<Long> postingIds);
}
