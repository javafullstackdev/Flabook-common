package com.msquare.flabook.service;

import lombok.AllArgsConstructor;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.models.Posting;
import com.msquare.flabook.models.PostingAttachment;
import com.msquare.flabook.models.Tag;
import com.msquare.flabook.repository.PostingAttachmentRepository;
import com.msquare.flabook.repository.PostingRepository;
import com.msquare.flabook.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AttachmentService {

    private final TagRepository tagRepository;
    private final PostingRepository postingRepository;
    private final PostingAttachmentRepository postingAttachmentRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean addTag(long id, String name) {

        //태그명 조회
        Tag tag = tagRepository.findByName(name).orElseThrow(() -> new FlabookGlobalException("태그를 찾을 수 없습니다."));

        //게시글 첨부파일 조회
        PostingAttachment attachment = postingAttachmentRepository.findById(id).orElseThrow(() -> new FlabookGlobalException("첨부파일을 찾을 수 없습니다."));

        //첨부파일의 게시글을 조회
        Posting posting = postingRepository.findLockOnly(attachment.getParent().getId()).orElseThrow(() -> new FlabookGlobalException("게시글을 찾을 수 없습니다."));

        //게시글 첨부파일에서 선택한 첨부파일을 찾아 태그 추가
        posting.getAttachments().forEach(a -> {
            if(a.getId() == id) {
                a.getTags().add(tag);
            }
        });

        //모든 첨부파일의 태그를 조회해 댓글의 태그 리스트 업데이트
        posting.getTags().addAll(posting.getAttachments().stream().flatMap(a -> a.getTags().stream()).collect(Collectors.toSet()));
        return true;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean removeTag(long id, String name) {
        Tag tag = tagRepository.findByName(name).orElseThrow(() -> new FlabookGlobalException("태그를 찾을 수 없습니다."));
        PostingAttachment attachment = postingAttachmentRepository.findById(id).orElseThrow(() -> new FlabookGlobalException("첨부파일을 찾을 수 없습니다."));
        Posting posting = postingRepository.findLockOnly(attachment.getParent().getId()).orElseThrow(() -> new FlabookGlobalException("게시글을 찾을 수 없습니다."));
        posting.getAttachments().forEach(a -> {
            if(a.getId() == id) {
                a.getTags().remove(tag);
            }
        });

        Set<Tag> tags = posting.getAttachments().stream().flatMap(a -> a.getTags().stream()).collect(Collectors.toSet());
        posting.getTags().removeIf(t -> !tags.contains(t));

        return true;
    }

}
