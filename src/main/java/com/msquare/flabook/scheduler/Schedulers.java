package com.msquare.flabook.scheduler;

import lombok.AllArgsConstructor;
import com.msquare.flabook.api.service.HomeService;
import com.msquare.flabook.api.service.RankingCacheService;
import com.msquare.flabook.dto.*;
import com.msquare.flabook.service.elasticsearch.CacheValues;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.msquare.flabook.api.service.HomeService.*;

@Component
@AllArgsConstructor
public class Schedulers {

    private final HomeService homeService;
    private final RankingCacheService rankingCacheService;

    public class HomeCacheJob {

        /**
         * 5초 마다 갱신
         * @return HomeTotalDto
         */
        @Scheduled(fixedDelay = 5000, initialDelay = 2000)
        @Caching(put = {
            @CachePut(cacheResolver = "localCacheResolver",value = HOME_TOTAL, key = "''"),
            @CachePut(cacheResolver = "localCacheResolver", value = HOME_TOTAL_LONG, key = "''")
        })
        public HomeTotalDto scheduler() {
            return homeService.all(null);
        }

        @Scheduled(fixedDelay = 5000)
        @CachePut(cacheResolver = "localCacheResolver",value = HOME_SUB, key = "'question'")
        public List<PostingDto> scheduler1() {
            return homeService.findCacheablePostingList(HomeService.Keys.QUESTION, false, null);
        }

        @Scheduled(fixedDelay = 5000)
        @CachePut(cacheResolver = "localCacheResolver",value = HOME_SUB, key = "'clinic'")
        public List<PostingDto> scheduler2() {
            return homeService.findCacheablePostingList(HomeService.Keys.CLINIC, false, null);
        }

        @Scheduled(fixedDelay = 5000)
        @CachePut(cacheResolver = "localCacheResolver",value = HOME_SUB, key = "'magazine'")
        public List<PostingDto> scheduler3() {
            return homeService.findCacheablePostingList(HomeService.Keys.MAGAZINE, false, null);
        }

        @Scheduled(fixedDelay = 5000)
        @CachePut(cacheResolver = "localCacheResolver",value = HOME_SUB, key = "'boast'")
        public List<PostingDto> scheduler4() {
            return homeService.findCacheablePostingList(HomeService.Keys.BOAST, false, null);
        }

        @Scheduled(fixedDelay = 5000)
        @CachePut(cacheResolver = "localCacheResolver",value = HOME_SUB, key = "'free'")
        public List<PostingDto> scheduler5() {
            return homeService.findCacheablePostingList(HomeService.Keys.FREE, false, null);
        }

        @Scheduled(fixedDelay = 5000)
        @CachePut(cacheResolver = "localCacheResolver",value = HOME_SUB, key = "'guidebook'")
        public List<PostingDto> scheduler6() {
            return homeService.findCacheablePostingList(HomeService.Keys.GUIDEBOOK, false, null);
        }

        @Scheduled(fixedDelay = 5000)
        @CachePut(cacheResolver = "localCacheResolver",value = HOME_SUB, key = "'television'")
        public List<PostingDto> scheduler7() {
            return homeService.findCacheablePostingList(HomeService.Keys.TELEVISION, false, null);
        }

        @Scheduled(fixedDelay = 5000)
        @CachePut(cacheResolver = "localCacheResolver",value = HOME_SUB, key = "'goods'")
        public List<GoodsDto> schedulerGoods() {
            return homeService.findCacheableGoodsList(false);
        }

        @Scheduled(fixedDelay = 5000)
        @CachePut(cacheResolver = "localCacheResolver",value = HOME_SUB, key = "'banner'")
        public List<BannerDto> schedulerBanner() {
            return homeService.findCacheableBannerList(false);
        }

        @Scheduled(fixedDelay = 5000)
        @CachePut(cacheResolver = "localCacheResolver",value = HOME_SUB, key = "'photo'")
        public List<PostingDto> scheduler8() {
            return homeService.findCacheablePostingList(HomeService.Keys.PHOTO, false, null);
        }
    }

    public class RankingCacheJob {
        @Scheduled(fixedDelay = 600000) //
        @CachePut(value = CacheValues.RANKING, key = "''", cacheResolver = "localCacheResolver")
        public RankingResponseBodyDto scheduler() {
            return rankingCacheService.findRanking(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Asia/Seoul")), false);
        }
    }

    @Bean
    @ConditionalOnProperty(value = "flabook.jobs.cache.home.enable", matchIfMissing = true, havingValue = "true")
    public HomeCacheJob homeScheduledJob() {
        return new HomeCacheJob();
    }

    @Bean
    @ConditionalOnProperty(value = "flabook.jobs.cache.ranking.enable", matchIfMissing = true, havingValue = "true")
    public RankingCacheJob rankingScheduledJob() {
        return new RankingCacheJob();
    }


}
