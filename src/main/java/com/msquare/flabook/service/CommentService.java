package com.msquare.flabook.service;

import com.drew.imaging.ImageProcessingException;
import com.vividsolutions.jts.geom.GeometryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.dto.AnswerDto;
import com.msquare.flabook.dto.CommentDto;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.exception.FlabookPermissionException;
import com.msquare.flabook.form.UpdateCommentVo;
import com.msquare.flabook.models.*;
import com.msquare.flabook.repository.*;
import com.msquare.flabook.service.elasticsearch.CacheValues;
import com.msquare.flabook.util.AccessControl;
import com.msquare.flabook.util.HashtagUtils;
import com.msquare.flabook.util.MentionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Tuple;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    final PostingRepository postingRepository;
    final UserRepository userRepository;
    final LikeCommentRepository likeCommentRepository;
    final EntityManagerFactory emf;
    final EntityManager em;
    final CommentRepository commentRepository;
    final GeometryFactory geometryFactory;
    final NotificationRepository notificationRepository;
    final ImageUploadService imageUploadService;
    final NotificationService notificationService;

    @Caching(evict = {
            @CacheEvict(value = CacheValues.COMMENT_LIST, key = "{#result.postingId}", condition = "#result.postingId != null")
    })
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public CommentDto updateComment(long id, UpdateCommentVo vo, User user) throws FlabookGlobalException, IOException, InterruptedException, ImageProcessingException {
        Comment comment = commentRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException("not found entity : " + id));

        if(!AccessControl.isAllowed(comment.getOwner(), user) || comment.isDelete() || comment.isBlind()) {
            throw new FlabookPermissionException("권한이 없습니다.");
        }

        if(vo.getText() != null && vo.getText().length() > Comment.MAX_TEXT_LENGTH) {
            throw new FlabookGlobalException("내용의 길이는 2048자로 제한됩니다.");
        }

        comment.setText(vo.getText());
        if(vo.getIds() == null) {
            if (vo.getFiles() != null && !vo.getFiles().isEmpty()) {
                ImageUploadService.ImageResourceInfoWithMetadata infoWithMetadata = imageUploadService.uploadWithMeta(FolderDatePatterns.COMMENTS, vo.getFiles());

                CommentAttachment attachment = CommentAttachment.builder().parent(comment)
                        .imageResource(infoWithMetadata.getImageResource()).dimension(infoWithMetadata.getDimension())
                        .location(infoWithMetadata.getPoint())
                        .build();
                comment.getAttachments().clear();
                comment.getAttachments().add(attachment);
            } else {
                comment.getAttachments().clear();
            }
        }

        List<Long> mentionIds = MentionUtils.extractMentions(vo.getText());
        if(!mentionIds.isEmpty()) {
            comment.getMentions().clear();
            comment.getMentions().addAll(commentRepository.findDistinctRecipientByMentionUserIds(comment.getPosting().getId(), mentionIds));
        }

        comment = commentRepository.saveAndFlush(comment);

        return CommentDto.of(comment).setPostingId(id);
    }

    //첫 페이지만 캐시
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    @Caching(cacheable = {
            @Cacheable(value = CacheValues.COMMENT_LIST, key = "{#postingId}", condition = "#sinceId == null && #maxId == null && #count == T(com.msquare.flabook.service.DefaultValues).DEFAULT_MAX_COMMENT_COUNT")
    })
    public List<CommentDto> findTimeline(long postingId, Long sinceId, Long maxId, int count) {

        StringBuilder queryBuffer = new StringBuilder();

        queryBuffer.append("select * from comment a join ( select comment0_.id id from comment comment0_ where comment0_.posting_id=");
        queryBuffer.append(postingId);

        if (sinceId != null && sinceId > 0) queryBuffer.append(" and id > ").append(sinceId);
        if (maxId != null && maxId > 0) queryBuffer.append(" and id <= ").append(maxId);

        queryBuffer.append(" and comment0_.parent_id is null order by id desc limit ");
        queryBuffer.append(count);
        queryBuffer.append(" ) b on a.id = b.id order by a.id desc");

        return ((List<Comment>)em.createNativeQuery(queryBuffer.toString(), Comment.class).getResultList()).stream().map(CommentDto::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Caching(cacheable = {
            @Cacheable(value = CacheValues.COMMENT_LIST, key = "{#postingId, 'first'}")
    })
    public Optional<CommentDto> findFirst(long postingId) {
        List<BigInteger> list = commentRepository.findFirstHashtagIds(Collections.singletonList(postingId));
        return commentRepository.findAllById(list.stream().map(BigInteger::longValue).collect(Collectors.toList())).stream()
            .filter(o -> {
                String text = o.getText();
                List<String> tags = HashtagUtils.extract(text);
                return !CollectionUtils.isEmpty(tags);
            }).map(CommentDto::of).findFirst();
    }

    @Transactional(readOnly = true)
    @Caching(cacheable = {
            @Cacheable(value = CacheValues.COMMENT_LIST, key = "{#postingId, 'adopt'}")
    })
    public Optional<CommentDto> findAdopted(long postingId) {
        return commentRepository.findAdoptedByPostingId(postingId).map(CommentDto::of);
    }

    @Cacheable(value = CacheValues.POSTINGS, key="{'answers', #postingIds}")
    public List<AnswerDto> findFirstHashtags(List<Long> postingIds) {
        List<Tuple> list = commentRepository.findFirstHashtags(postingIds);
        return list.stream().map(o -> {
            String text = (String)o.get(0);
            List<String> tags = HashtagUtils.extract(text);
            if(!CollectionUtils.isEmpty(tags))
                text = tags.get(0);

            return new AnswerDto(text, new String((byte[])o.get(1), StandardCharsets.UTF_8), ((BigInteger)o.get(2)).longValue(), ((Byte)o.get(3)).intValue());
        }).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean toggleLike(long id, Long userId) {

        Comment comment = commentRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException("not found Entity"));
        UserCommentRelation pk = new UserCommentRelation(userRepository.getOne(userId), comment);

        //optional 로 처리시 query 가 2 번 나감 (select 할 때, .get() 할 때)
        LikeComment userLike = likeCommentRepository.findByIdEquals(pk);
        if (userLike != null) {
            likeCommentRepository.delete(userLike);
            comment.setLikeCount(comment.getLikeCount() - 1);
            return false;
        } else {
            userLike = likeCommentRepository.save(new LikeComment(pk));
            comment.setLikeCount(comment.getLikeCount() + 1);
            notificationService.afterNewLike(userLike, comment.getOwner());
            return true;
        }
    }

}
