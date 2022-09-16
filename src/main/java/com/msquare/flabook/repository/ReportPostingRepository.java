package com.msquare.flabook.repository;

import java.util.List;

import com.msquare.flabook.enumeration.ReportStatus;
import com.msquare.flabook.models.Posting;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.msquare.flabook.models.ReportPosting;
import com.msquare.flabook.models.UserPostingRelation;

import javax.persistence.Tuple;

public interface ReportPostingRepository extends JpaRepository<ReportPosting, Long> {

    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from ReportPosting l where l.relation = :relation ")
    ReportPosting findByIdEquals(@Param("relation") UserPostingRelation relation);

    @Query("select rp from ReportPosting  rp join rp.relation.posting p where p.isDelete = false")
    //@Query(nativeQuery = true, value = "select rp.* from report_posting rp inner join posting p on rp.posting_id = p.id where p.is_delete = false")
//    @Query("select l from ReportPosting l join Posting p on l.relation.posting.id = p.id")
    List<ReportPosting> findAllReportList(Pageable request);

    @Query("select p.id, count(p.id) from ReportPosting  rp join rp.relation.posting p where p.id in (postingIds) group by p.id")
    List<Tuple> countGroupByPostings(List<Long> postingIds);

    @Query("select l.relation.user.id as uid , count(l.relation.user.id) as cnt from ReportPosting l where l.relation.user.id in (:userIds) group by uid")
    List<Tuple> countGroupByUsers(@Param("userIds")List<Long> userIds);

    @Modifying
    @Query("update ReportPosting set reportStatus = :status where relation.posting.id = :#{#posting.id}")
    int updateAll(@Param("posting") Posting posting, @Param("status") ReportStatus status);

    @Modifying
    @Query("update ReportPosting rp set rp.reportStatus = :status where rp.id = :#{#report.id}")
    int updateReportStatus(@Param("report") ReportPosting report, @Param("status") ReportStatus status);
}
