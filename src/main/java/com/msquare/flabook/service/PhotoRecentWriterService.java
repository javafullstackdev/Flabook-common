package com.msquare.flabook.service;

import lombok.RequiredArgsConstructor;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.models.PhotoRecentWriter;
import com.msquare.flabook.repository.PhotoRecentWriterRepository;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Service
@RequiredArgsConstructor
public class PhotoRecentWriterService {

    private final PhotoRecentWriterRepository photoRecentWriterRepository;
    private final EntityManager em;

    @Transactional
    public void checkAndRemove(Long userId) {
        photoRecentWriterRepository.findIdByUserId(userId).ifPresent(photoRecentWriterRepository::delete);
    }

    @Transactional
    public void photoRecentWriterIndexing(Long id) {
        PhotoRecentWriter photoRecentWriter = photoRecentWriterRepository.findById(id).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY+id));

        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
        FullTextSession session = fullTextEntityManager.unwrap(FullTextSession.class);
        session.index(photoRecentWriter);
        session.flush();
        session.clear();
    }

    @Transactional
    public void checkAndRemovePosting(Long postingId) {
        photoRecentWriterRepository.findIdByPostingId(postingId).ifPresent(photoRecentWriterRepository::delete);
    }

    @Transactional
    public Long findIdByUserId(long id) {
        return photoRecentWriterRepository.findIdByUserId(id).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY)).getId();
    }
}
