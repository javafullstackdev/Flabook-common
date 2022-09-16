package com.msquare.flabook.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.enumeration.ReportStatus;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.form.CreateReportVo;
import com.msquare.flabook.models.Comment;
import com.msquare.flabook.models.ReportComment;
import com.msquare.flabook.models.UserCommentRelation;
import com.msquare.flabook.repository.CommentRepository;
import com.msquare.flabook.repository.ReportCommentRepository;
import com.msquare.flabook.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class ReportCommentService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReportCommentRepository reportCommentRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean addReport(long id, long userId, CreateReportVo vo) {
        Comment comment = commentRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException("not found Entity"));
        UserCommentRelation pk = new UserCommentRelation(userRepository.getOne(userId), comment);

        //optional 로 처리시 query 가 2 번 나감 (select 할 때, .get() 할 때)
        ReportComment userLike = reportCommentRepository.findByIdEquals(pk);
        if (userLike == null) {
            commentRepository.updateLikeCount(id, 1);
            reportCommentRepository.save((ReportComment)new ReportComment(pk).setTitle(vo.getTitle()).setText(vo.getText()).setReportStatus(ReportStatus.WAIT));
            return true;
        }
        return false;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean removeReport(long id, long userId) {
        Comment comment = commentRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException("not found Entity"));
        UserCommentRelation pk = new UserCommentRelation(userRepository.getOne(userId), comment);

        //optional 로 처리시 query 가 2 번 나감 (select 할 때, .get() 할 때)
        ReportComment userLike = reportCommentRepository.findByIdEquals(pk);
        if (userLike != null) {
            commentRepository.updateLikeCount(id, -1);
            reportCommentRepository.delete(userLike);
            return false;
        }
        return true;
    }


}

