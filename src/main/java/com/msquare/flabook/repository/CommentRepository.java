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
     * ???????????? ????????? ????????? ?????????(????????? ??????)
     * @param postingId ????????????Id
     * @return List<User> ?????? ????????? ?????? ????????? ??????
     */
   // @Query(nativeQuery = true, value= "select distinct u.* from comment c join user u on c.owner_id = u.id  where c.posting_id = :postingId and c.parent_id is null")
    @Query("select distinct c.owner from Comment c where c.posting.id = :postingId and c.parent is null and c.isDelete = false")
    List<User> findDistinctRecipientWithoutReplyList(@Param("postingId") long postingId);

    /**
     * ???????????? ????????? ????????? ?????????
     * @param postingId ????????????Id
     * @return List<User> ?????? ????????? ?????? ????????? ??????
     */
    // @Query(nativeQuery = true, value= "select distinct u.* from comment c join user u on c.owner_id = u.id  where c.posting_id = :postingId and c.parent_id is null")
    @Query("select distinct c.owner from Comment c where c.owner.nickname like %:query% and c.posting.id = :postingId and c.isDelete = false")
    List<User> findDistinctRecipientList(@Param("postingId") long postingId, @Param("query") String query, Sort sort);

    /**
     * ???????????? ????????? ????????? ?????????
     * @param postingId ????????????Id
     * @return List<User> ?????? ????????? ?????? ????????? ??????
     */
    @Query("select distinct c.owner from Comment c where c.posting.id = :postingId and c.isDelete = false")
    List<User> findDistinctRecipientList(@Param("postingId") long postingId);

    /**
     * ???????????? ???????????? ?????? ??????????????? ?????? ????????? ????????? ????????? ?????????
     * @param postingId ?????????ID
     * @return List<User> ?????? ?????? ????????? ??????
     */
    @Query("select distinct o from Comment c join c.owner o join o.userSetting s where c.posting.id = :postingId and c.isDelete = false and s.joinCommentNotiEnable = true")
    List<User> findDistinctJoinPushEnabledRecipientList(@Param("postingId") long postingId);

    /**
     * ???????????? ????????? ????????? ??? ????????? ????????? id ??? ????????? ????????? ?????????
     * @param postingId ?????????ID
     * @param mentionUserIds ????????? ????????? ID
     * @return List<User> ????????? ????????? ??????
     */
    @Query("select distinct c.owner from Comment c where c.posting.id = :postingId and c.owner.id in (:mentionUserIds) and c.isDelete = false")
    List<User> findDistinctRecipientByMentionUserIds(@Param("postingId") long postingId, @Param("mentionUserIds") List<Long> mentionUserIds);

    @Query(nativeQuery = true, value="select c.text as text, u.nickname as nickname, c.posting_id as postingId, u.level as level, u.id as uid from  (\n" +
            "        select min(c.id) id from comment c\n" +
            "        where c.posting_id in (:postingIds) and c.is_delete = false and c.is_blind = false and c.text  RLIKE '#[a-z|A-Z|???-???|_|]+'\n" +
            "        group by c.posting_id\n" +
            "    ) cc join comment c on c.id = cc.id\n" +
            "    join user u on u.id = c.owner_id")
    List<Tuple> findFirstHashtags(@Param("postingIds") List<Long> postingIds);

    @Query(nativeQuery = true, value="select c.id from  (\n" +
            "        select min(c.id) id from comment c\n" +
            "        where c.posting_id in (:postingIds) and c.is_delete = false and c.is_blind = false and c.text  RLIKE '#[a-z|A-Z|???-???|_|]+'\n" +
            "        group by c.posting_id\n" +
            "    ) cc join comment c on c.id = cc.id\n" +
            "    join user u on u.id = c.owner_id")
    List<BigInteger> findFirstHashtagIds(@Param("postingIds") List<Long> postingIds);
}
