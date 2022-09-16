package com.msquare.flabook.api.service;

import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.dto.RankingResponseBodyDto;
import com.msquare.flabook.service.RankingService;
import com.msquare.flabook.service.elasticsearch.CacheValues;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Slf4j
@Service
public class RankingCacheServiceImpl implements RankingCacheService {

    private final RankingService rankingService;


    public RankingCacheServiceImpl(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @Transactional(readOnly = true)
    @Caching(
        cacheable = {
            @Cacheable(value = CacheValues.RANKING, key = "''", cacheResolver = "localCacheResolver", condition = "#cacheable == true")
        } ,
        evict = {
            @CacheEvict(value = CacheValues.RANKING, key = "''", cacheResolver = "localCacheResolver", condition = "#cacheable != true")
        }
    )
    public RankingResponseBodyDto findRanking(ZonedDateTime date, boolean cacheable) {
        return rankingService.findRanking(date);
    }

}
