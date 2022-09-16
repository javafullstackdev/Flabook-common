package com.msquare.flabook.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.enumeration.ReportStatus;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.form.CreateReportVo;
import com.msquare.flabook.models.Posting;
import com.msquare.flabook.models.ReportPosting;
import com.msquare.flabook.models.UserPostingRelation;
import com.msquare.flabook.repository.PostingRepository;
import com.msquare.flabook.repository.ReportPostingRepository;
import com.msquare.flabook.repository.UserRepository;

@Slf4j
@Service
@AllArgsConstructor
public class ReportPostingService {

    private final UserRepository userRepository;
    private final PostingRepository postingRepository;
    private final ReportPostingRepository reportPostingRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean addReport(long id, long userId, CreateReportVo vo) {
        Posting posting = postingRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException("not found Entity"));

        UserPostingRelation pk = new UserPostingRelation(userRepository.getOne(userId), posting);

        //optional 로 처리시 query 가 2 번 나감 (select 할 때, .get() 할 때)
        ReportPosting reportPosting = reportPostingRepository.findByIdEquals(pk);
        if (reportPosting == null) {
            postingRepository.updateReportCount(id, 1);
            reportPostingRepository.save((ReportPosting) new ReportPosting(posting.getPostingType(), pk).setTitle(vo.getTitle()).setText(vo.getText()).setReportStatus(ReportStatus.WAIT));
        } else {
            reportPosting.setPostingType(posting.getPostingType());
            reportPosting.setTitle(vo.getTitle());
            reportPosting.setText(vo.getText());
        }
        return true;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean removeReport(long id, long userId) {
        Posting posting = postingRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException("not found Entity"));
        UserPostingRelation pk = new UserPostingRelation(userRepository.getOne(userId), posting);

        //optional 로 처리시 query 가 2 번 나감 (select 할 때, .get() 할 때)
        ReportPosting userLike = reportPostingRepository.findByIdEquals(pk);
        if (userLike != null) {
            postingRepository.updateReportCount(id, -1);
            reportPostingRepository.delete(userLike);
        }
        return true;
    }

}

