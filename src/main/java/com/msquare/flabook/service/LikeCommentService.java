package com.msquare.flabook.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.models.Comment;
import com.msquare.flabook.models.LikeComment;
import com.msquare.flabook.models.UserCommentRelation;
import com.msquare.flabook.repository.CommentRepository;
import com.msquare.flabook.repository.LikeCommentRepository;
import com.msquare.flabook.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class LikeCommentService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeCommentRepository likeCommentRepository;
    private final NotificationService notificationService;

    @SuppressWarnings("unused")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean toggleLike(long id, long userId) {

        Comment comment = commentRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY));
        UserCommentRelation pk = new UserCommentRelation(userRepository.getOne(userId), comment);

        //optional 로 처리시 query 가 2 번 나감 (select 할 때, .get() 할 때)
        LikeComment userLike = likeCommentRepository.findByIdEquals(pk);
        if (userLike != null) {
            likeCommentRepository.delete(userLike);
            commentRepository.updateLikeCount(id, -1);
            return false;
        } else {
            userLike = likeCommentRepository.save(new LikeComment(pk));
            commentRepository.updateLikeCount(id, 1);
            notificationService.afterNewLike(userLike, comment.getOwner());
            return true;
        }
    }

    @SuppressWarnings("unused")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean addLike(long id, long userId) {
        Comment comment = commentRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY));
        UserCommentRelation pk = new UserCommentRelation(userRepository.getOne(userId), comment);

        //optional 로 처리시 query 가 2 번 나감 (select 할 때, .get() 할 때)
        LikeComment userLike = likeCommentRepository.findByIdEquals(pk);
        if (userLike == null) {
            userLike = likeCommentRepository.save(new LikeComment(pk));
            commentRepository.updateLikeCount(id, 1);
            notificationService.afterNewLike(userLike, comment.getOwner());
            return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean removeLike(long id, long userId) {
        Comment comment = commentRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY));
        UserCommentRelation pk = new UserCommentRelation(userRepository.getOne(userId), comment);

        //optional 로 처리시 query 가 2 번 나감 (select 할 때, .get() 할 때)
        LikeComment userLike = likeCommentRepository.findByIdEquals(pk);
        if (userLike != null) {
            likeCommentRepository.delete(userLike);
            commentRepository.updateLikeCount(id, -1);
            return false;
        }
        return true;
    }

}

