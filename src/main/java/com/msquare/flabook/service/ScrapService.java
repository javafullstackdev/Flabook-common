package com.msquare.flabook.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.models.Posting;
import com.msquare.flabook.models.ScrapPosting;
import com.msquare.flabook.models.User;
import com.msquare.flabook.models.UserPostingRelation;
import com.msquare.flabook.repository.PostingRepository;
import com.msquare.flabook.repository.ScrapRepository;
import com.msquare.flabook.repository.UserRepository;
import com.msquare.flabook.service.elasticsearch.CacheValues;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.msquare.flabook.util.ValidateNotPhotoUtils.validateNotPhoto;

@Slf4j
@Service
@AllArgsConstructor
public class ScrapService {

    private final UserRepository userRepository;
    private final PostingRepository postingRepository;
    private final ScrapRepository scrapRepository;


    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Caching(evict = {
            @CacheEvict(value = CacheValues.POSTINGS, key = "#id")
    })
    public boolean addScrap(long id, long userId) {
        UserPostingRelation pk = new UserPostingRelation(userRepository.getOne(userId), postingRepository.getOne(id));

        //optional 로 처리시 query 가 2 번 나감 (select 할 때, .get() 할 때)
        ScrapPosting userScrap = scrapRepository.findByIdEquals(pk);
        if (userScrap == null) {
            Posting posting = postingRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException("not found Entity"));
            validateNotPhoto(posting);
            postingRepository.updateScrapCount(posting.getId(), 1);
            postingRepository.updateReadCount(posting.getId(), 1);
            scrapRepository.save(new ScrapPosting(pk, posting.getPostingType()));
        }
        return true;
    }

    //@CacheEvict(value = "clipping", key = "{#id, #userId}")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Caching(evict = {
            @CacheEvict(value = CacheValues.POSTINGS, key = "#id")
    })
    public boolean removeScrap(long id, long userId) {
        UserPostingRelation pk = new UserPostingRelation(userRepository.getOne(userId), postingRepository.getOne(id));

        //optional 로 처리시 query 가 2 번 나감 (select 할 때, .get() 할 때)
        ScrapPosting userScrap = scrapRepository.findByIdEquals(pk);
        if (userScrap != null) {
            Posting posting = postingRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException("not found Entity"));
            postingRepository.updateScrapCount(posting.getId(), -1);
            scrapRepository.delete(userScrap);
        }
        return false;
    }

    public boolean isScrap(long id, User owner) {
        UserPostingRelation pk = new UserPostingRelation(userRepository.getOne(owner.getId()), postingRepository.getOne(id));
        return scrapRepository.findByIdEquals(pk) != null;
    }

    public Map<Long, Boolean> findListByUser(@NonNull List<Long> ids, User owner) {

        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        return Collections.unmodifiableMap(scrapRepository.findScraps(ids, owner.getId()).stream()
                .collect(Collectors.toMap(tuple -> ((Long) tuple.get("id")), tuple -> true)));

    }
}
