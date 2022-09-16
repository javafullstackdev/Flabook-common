package com.msquare.flabook.repository;

import com.msquare.flabook.enumeration.ReportStatus;
import com.msquare.flabook.models.Comment;
import com.msquare.flabook.models.ReportComment;
import com.msquare.flabook.models.UserCommentRelation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.util.List;

public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {

    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from ReportComment l where l.relation = :relation ")
    ReportComment findByIdEquals(@Param("relation") UserCommentRelation relation);

    @Query("select l.relation.user.id as uid , count(l.relation.user.id) as cnt from ReportComment l where l.relation.user.id in (:userIds) group by uid")
    List<Tuple> countGroupByUsers(@Param("userIds")List<Long> userIds);

    @Query("select rc from ReportComment rc join rc.relation.comment c where c.isDelete = false")
        //@Query(nativeQuery = true, value = "select rp.* from report_posting rp inner join posting p on rp.posting_id = p.id where p.is_delete = false")
//    @Query("select l from ReportPosting l join Posting p on l.relation.posting.id = p.id")
    List<ReportComment> findAllReportList(Pageable request);

    @Modifying
    @Query("update ReportComment rc set rc.reportStatus = :status where rc.relation.comment.id = :#{#comment.id}")
    int updateAll(@Param("comment")Comment comment, @Param("status") ReportStatus status);
}
