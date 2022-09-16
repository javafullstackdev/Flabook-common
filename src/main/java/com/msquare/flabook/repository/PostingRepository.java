package com.msquare.flabook.repository;

import com.msquare.flabook.models.Posting;
import com.msquare.flabook.models.shop.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PostingRepository extends JpaRepository<Posting, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Posting p where p.id = :id")
    Optional<Posting> findLockOnly(@Param("id") long id);

    @SuppressWarnings("unused")
    @Query("select p from Posting p where p.id in (:ids) and p.createdAt between :from and :to")
    List<Posting> findAllByIdWithDateRange(@Param("ids") Collection<Long> ids, @Param("from") ZonedDateTime from, @Param("to") ZonedDateTime to);

    /**
     * hibernate search 업데이트 안됨
     * cacheevict 실행됨
     *
     * @param id id
     * @param sum 추가값
     * @return 결과
     */
    @Modifying
    @SuppressWarnings("UnusedReturnValue")
    @Query(value = "UPDATE Posting p set p.readCount = p.readCount + :sum where p.id = :id")
    int updateReadCount(@Param("id")  long id, @Param("sum") int sum);

    @Modifying
    @SuppressWarnings("UnusedReturnValue")
    @Query(value = "UPDATE Posting p set p.receivingCount = p.receivingCount + :sum where p.id = :id")
    int updateReceivingCount(@Param("id") long id, @Param("sum") int sum);

    @Modifying
    @SuppressWarnings("UnusedReturnValue")
    @Query(value = "UPDATE Posting p set p.commentCount = p.commentCount + :sum where p.id = :id")
    int updateCommentCount(@Param("id") long id, @Param("sum") int sum);

    @Modifying
    @SuppressWarnings("UnusedReturnValue")
    @Query(value = "UPDATE Posting p set p.likeCount = p.likeCount + :sum where p.id = :id")
    int updateLikeCount(@Param("id") long id, @Param("sum")  int sum);

    @Modifying
    @SuppressWarnings("UnusedReturnValue")
    @Query(value = "UPDATE Posting p set p.scrapCount = p.scrapCount + :sum where p.id = :id")
    int updateScrapCount(@Param("id") long id, @Param("sum")  int sum);

    @Modifying
    @SuppressWarnings("UnusedReturnValue")
    @Query(value = "UPDATE Posting p set p.reportCount = p.reportCount + :sum where p.id = :id")
    int updateReportCount(@Param("id")long id, @Param("sum") int sum);

    @SuppressWarnings("unused")
    @Query("select p from LikePosting l join Posting p on l.relation.posting.id = p.id where p.owner.id = :id")
    List<Posting> findLikePostsByUser(@Param("id") long id, Pageable request);

    @Query("select p from ScrapPosting l join Posting p on l.relation.posting.id = p.id where p.owner.id = :id")
    List<Posting> findScrapPostsByUser(@Param("id") long id, Pageable request);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @SuppressWarnings("UnusedReturnValue")
    @Query(nativeQuery = true, value = "UPDATE posting p set p.dtype = :discriminatorValue where id in (:ids)")
    int updateBoardType(@Param("ids") List<Long> ids, @Param("discriminatorValue") String discriminatorValue);

    @Query("select p.owner.id = :userId from Posting p where id = :postingId")
    boolean isPostingOwner(@Param("postingId")long postingId, @Param("userId")Long userId);

    @Query("select p.goodses from Posting p where p.id = :id")
    List<Goods> findRelationGoodses(@Param("id")long id);

    @Query(value = "select * from posting p where p.id in (select ppa.photo_id from photo_photo_album ppa where ppa.photoalbum_id = :photoAlbumId) order by p.like_count desc limit 4", nativeQuery = true)
    List<Posting> findRepresentPhotosByPhotoAlbumId(@Param("photoAlbumId") Long photoAlbumId);

    @Query(value = "select * from posting p where p.id in (select ppa.photo_id from photo_photo_album ppa where ppa.photoalbum_id = :photoAlbumId) order by p.id desc limit 3", nativeQuery = true)
    List<Posting> findWriterRepresentPhotosByPhotoAlbumId(@Param("photoAlbumId") Long photoAlbumId);

    @Query(value = "select * from posting p where :before <= p.created_at and p.created_at <= :now and p.dtype ='PH'", nativeQuery = true)
    List<Posting> findPopularWriter(@Param("before") ZonedDateTime before,@Param("now") ZonedDateTime now);

    @Query(value = "select * from posting p where p.dtype ='PH' order by id desc limit :count", nativeQuery = true)
    List<Posting> findPhotoPostingsByLimit(@Param("count") int count);

    @Query(value = "select * from posting p where p.id in ( select ppa.photo_id from photo_photo_album ppa where ppa.photoalbum_id = :photoAlbumId )", nativeQuery = true)
    Page<Posting> findAllByPhotoAlbum(@Param("photoAlbumId") Long photoAlbumId, Pageable pageable);

}
