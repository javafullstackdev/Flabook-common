package com.msquare.flabook.api.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.dto.BannerDto;
import com.msquare.flabook.dto.GoodsDto;
import com.msquare.flabook.dto.HomeTotalDto;
import com.msquare.flabook.dto.PostingDto;
import com.msquare.flabook.enumeration.BannerStatus;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.models.Home;
import com.msquare.flabook.models.Ranking;
import com.msquare.flabook.models.User;
import com.msquare.flabook.repository.HomeRepository;
import com.msquare.flabook.service.BannerService;
import com.msquare.flabook.service.PostingService;
import com.msquare.flabook.service.RankingService;
import com.msquare.flabook.service.ShopService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HomeService {

    public static final String HOME_TOTAL = "home_total";
    public static final String EMPTY_STRING = "";
    public static final String ID = "id";
    public static final String HOME_SUB = "home_sub";
    public static final String HOME_TOTAL_LONG = "home_total_long";

    public static final HomeTotalDto EMPTY_HOME_TOTAL = new HomeTotalDto(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

    private final PostingService postingService;
    private final ShopService shopService;
    private final HomeRepository homeRepository;
    private final BannerService bannerService;
    private final CacheManager cacheManager;
    private final HomeService self;
    private final RankingService rankingService;

    public HomeService(PostingService postingService, ShopService shopService, HomeRepository homeRepository, BannerService bannerService, @Qualifier("localCacheManager") CacheManager cacheManager, @Lazy HomeService homeService, RankingService rankingService) {
        this.postingService = postingService;
        this.shopService = shopService;
        this.homeRepository = homeRepository;
        this.bannerService = bannerService;
        this.cacheManager = cacheManager;
        this.self = homeService;
        this.rankingService = rankingService;
    }

    public static final class Keys {

        private Keys() {
            //
        }

        public static final String QUESTION = "question";
        public static final String MAGAZINE = "magazine";
        public static final String CLINIC = "clinic";
        public static final String BOAST = "boast";
        public static final String FREE = "free";
        public static final String GUIDEBOOK = "guidebook";
        public static final String TELEVISION = "television";
        public static final String PHOTO = "photo";
    }


    @Caching(cacheable = {
        @Cacheable(cacheResolver = "localCacheResolver",value = HOME_TOTAL, key = "''")
    })

    public HomeTotalDto findAll(User currentUser) {
        if(log.isDebugEnabled()) {
            log.debug("home find all user : {}", currentUser.getId());
        }

        Cache cache = cacheManager.getCache(HOME_TOTAL_LONG);
        if(cache == null) {
            return EMPTY_HOME_TOTAL;
        }

        HomeTotalDto total = cache.get("", HomeTotalDto.class);
        if(total != null) {
            return total;
        }

        return EMPTY_HOME_TOTAL;
    }

    /**
     * HOME CRUD
     * */
    public void createHomeGenre(String genre){
        Home home = new Home();
        home.setGenre(genre);
        home.setOrderCount((int) homeRepository.count()+1);
        homeRepository.save(home);
    }

    @Cacheable(cacheResolver = "localCacheResolver",value = HOME_TOTAL, key = "'order'")
    public List<String> getHomeOrder(){ //순서별 Home List<String>
        List<Home> homeList = homeRepository.findAll();
        homeList.sort(Comparator.comparingInt(Home::getOrderCount));

        return homeList.stream()
                .map(Home::getGenre)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unused")
    @Transactional
    public void updateHomeGenre(String genre){
        Home home = homeRepository.findByGenre(genre).orElse(null);
        if(home==null) return;
        home.setGenre(genre);
    }
    @Transactional
    public void updateHomeOrder(List<String> genres){
        int orderCnt=1;
        for(String genre : genres){
            if(genre.isEmpty())
                continue;

            Home home = homeRepository.findByGenre(genre).orElse(null);
            //해당 장르가 db에 없다면 생성해서 저장해주기
            if(home==null){
                Home home1 = new Home();
                home1.setGenre(genre);
                home1.setOrderCount(orderCnt++);
                homeRepository.save(home1);
            } else {
                home.setOrderCount(orderCnt++);
            }
        }

        //genres에 없었다면 지우기
        List<Home> homeList = homeRepository.findAll();
        for(Home home : homeList){
            if( !genres.contains(home.getGenre()) ) {
                homeRepository.delete(home);
            }
        }
    }

    @SuppressWarnings("unused")
    public void deleteHomeGenre(String genre){
        homeRepository.findByGenre(genre).ifPresent(homeRepository::delete);
    }

    @SneakyThrows
    @Cacheable(cacheResolver = "localCacheResolver",value = HOME_SUB, key = "#key", condition = "#cacheable == true")
    public List<PostingDto> findCacheablePostingList(String key, boolean cacheable, User currentUser) {
        log.debug("info : {}, cacheable : {}", key, cacheable);
        try {
            switch (key) {
                case HomeService.Keys.QUESTION:
                    return postingService.findTimeline(currentUser, PostingType.question.getClazz(), 0L, 0L, 9);

                case HomeService.Keys.MAGAZINE:
                    return postingService.findTimeline(currentUser, PostingType.magazine.getClazz(), 0L, 0L, 10);
                case HomeService.Keys.BOAST:
                    return postingService.findTimeline(currentUser, PostingType.boast.getClazz(), 0L, 0L, 6);

                case HomeService.Keys.FREE:
                    return postingService.findTimeline(currentUser, PostingType.free.getClazz(), 0L, 0L, 3);

                case HomeService.Keys.CLINIC:
                    return postingService.findTimeline(currentUser, PostingType.clinic.getClazz(), 0L, 0L, 3);

                case HomeService.Keys.GUIDEBOOK:
                    return postingService.findTimeline(currentUser, PostingType.guidebook.getClazz(), 0L, 0L, 10);

                case HomeService.Keys.TELEVISION:
                    return postingService.findTimeline(currentUser, PostingType.television.getClazz(), 0L, 0L, 10);

                case HomeService.Keys.PHOTO:
                    return rankingService.findPostingRanking(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Asia/Seoul")), Ranking.RankingType.best_photo);

                default:
                    return Collections.emptyList();
            }
        } catch(Exception e) {
            return Collections.emptyList();
        }
    }


    @Cacheable(cacheResolver = "localCacheResolver",value = HOME_SUB, key = "'banner'", condition = "#cacheable == true")
    public List<BannerDto> findCacheableBannerList(boolean cacheable) {
        return bannerService.findList(0, 10, Optional.of(BannerStatus.open));
    }

    @Cacheable(cacheResolver = "localCacheResolver",value = HOME_SUB, key = "'goods'", condition = "#cacheable == true")
    public List<GoodsDto> findCacheableGoodsList(boolean cacheable) {
        return shopService.searchList(0, 10, EMPTY_STRING, Optional.of(true), ID);
    }


    /**
     * self invocation 으로 같은 bean 에서 cacheable 이 동작하지 않으므로 homeService 에서 cacheService 를 호출하도록 분리
     *
     * @param currentUser 사용자 구분을 하지 않아 현재는 사용안함
     * @return HomeTotalDto
     */
    public HomeTotalDto all(User currentUser) {
        if(log.isDebugEnabled()) {
            log.debug("home all read user : {}", currentUser.getId());
        }

        List<PostingDto> questionList = self.findCacheablePostingList(Keys.QUESTION, true, currentUser);
        List<PostingDto> magazineList = self.findCacheablePostingList(Keys.MAGAZINE, true, currentUser);
        List<PostingDto> boastList = self.findCacheablePostingList(Keys.BOAST, true, currentUser);
        List<PostingDto> freeList = self.findCacheablePostingList(Keys.FREE, true, currentUser);
        List<PostingDto> guidebookList = self.findCacheablePostingList(Keys.GUIDEBOOK, true, currentUser);
        List<PostingDto> clinicList = self.findCacheablePostingList(Keys.CLINIC, true, currentUser);
        List<PostingDto> televisionList = self.findCacheablePostingList(Keys.TELEVISION, true, currentUser);
        List<PostingDto> photoList = self.findCacheablePostingList(Keys.PHOTO, true, currentUser);
        List<GoodsDto> goodsList = self.findCacheableGoodsList(true);
        List<BannerDto> bannerList = self.findCacheableBannerList(true);
        return new HomeTotalDto(questionList, magazineList, clinicList, guidebookList, freeList, boastList, televisionList, goodsList, bannerList, photoList);
    }


}
