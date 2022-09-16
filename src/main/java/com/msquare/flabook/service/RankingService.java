package com.msquare.flabook.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.dto.PostingDto;
import com.msquare.flabook.dto.RankingDto;
import com.msquare.flabook.dto.RankingResponseBodyDto;
import com.msquare.flabook.dto.UserDto;
import com.msquare.flabook.models.*;
import com.msquare.flabook.models.board.Photo;
import com.msquare.flabook.models.shop.Goods;
import com.msquare.flabook.repository.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Slf4j
@Service
@AllArgsConstructor
public class RankingService {

    private static final String DATE_FORMAT_MONTH_DAY = "MM.dd";
    private static final List<String> genreList = Arrays.asList("posting", "goods", "user", "keyword");

    private final PostingRankingRepository postingRankingRepository;
    private final UserRankingRepository userRankingRepository;
    private final KeywordRankingRepository keywordRankingRepository;
    private final RankingLogRepository rankingLogRepository;
    private final GoodsRankingRepository goodsRankingRepository;
    private final GoodsRepository goodsRepository;
    private final RankingOrderRepository rankingOrderRepository;

    @Data
    @AllArgsConstructor
    private static class RankingRaw {

        private IRanking ranking;

        private int rank;

    }

    private String rankNum(int pos) {
        if(pos == 0)
            return "-";

        if(pos > 0) {
            return String.format("+%d", pos);
        } else {
            return String.format("-%d", pos);
        }
    }

    private List<RankingDto> compare(List<? extends IRanking> beforeRankings, List<? extends IRanking> lastRankings) {
        beforeRankings.sort((o1, o2) -> NumberUtils.compare(o2.getScore(), o1.getScore()));
        lastRankings.sort((o1, o2) -> NumberUtils.compare(o2.getScore(), o1.getScore()));

        List<RankingRaw> r2 = new ArrayList<>();

        int seq = 1;
        for(IRanking ranking : beforeRankings) {
            r2.add(new RankingRaw(ranking, seq));
            seq++;
        }

        Map<String, RankingRaw> r2Map = r2.stream().collect(Collectors.toMap(raw -> raw.getRanking().getKey(), raw -> raw));

        List<RankingDto> rankingDtos = new ArrayList<>();
        seq = 1;
        for(IRanking ranking : lastRankings) {
            RankingRaw be = r2Map.get(ranking.getKey());

            if(log.isDebugEnabled()) {
                Integer pos = (be == null) ? null : be.rank - seq ;
                String v = (pos == null) ? "N" : rankNum(pos);
                log.debug("{}. {} {}", seq, ranking.getTarget(), v);
            }

            RankingDto r1 = (be != null) ? RankingDto.of(be.getRanking()) : null;
            if(r1 != null) {
                r1.setRank(be.rank);
            }
            RankingDto r3 = RankingDto.of(ranking);
            r3.setRank(seq);
            rankingDtos.add(RankingDto.of(r1, r3));
            seq ++;
        }

        return rankingDtos;
    }

    @Transactional(readOnly = true)
    public RankingResponseBodyDto findRanking(ZonedDateTime date) {

        Optional<RankingLog> lastRankingLog = rankingLogRepository.findLastByDate(date);

        ZonedDateTime last = lastRankingLog.map(RankingLog::getDate).map(dt -> dt.withZoneSameInstant(ZoneId.of("Asia/Seoul"))).orElse(ZonedDateTime.now());
        ZonedDateTime before = rankingLogRepository.findBeforeByDate(last).map(RankingLog::getDate).map(dt -> dt.withZoneSameInstant(ZoneId.of("Asia/Seoul"))).orElse(ZonedDateTime.now());

        String strDate;
        ZonedDateTime from = last.minusDays(6);

        DateTimeFormatter monthDayFormat = DateTimeFormatter.ofPattern(DATE_FORMAT_MONTH_DAY);

        if(from.getYear() < last.getYear()) {
            strDate = from.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + "~" +last.format(monthDayFormat);
        } else {
            strDate = from.format(monthDayFormat) + "~" +last.format(monthDayFormat);
        }

        return new RankingResponseBodyDto(
                strDate,
                findRanking(before, last, Ranking.RankingType.best_keyword),
                findRanking(before, last,  Ranking.RankingType.best_user_by_question),
                findRanking(before, last, Ranking.RankingType.best_user_by_clinic),
                findRanking(before, last,  Ranking.RankingType.best_posting),
                findRanking(before, last,  Ranking.RankingType.best_goods)
        );
    }

    @SuppressWarnings("java:S3776")
    @Transactional(readOnly = true)
    public List<RankingDto> findRanking(ZonedDateTime before, ZonedDateTime last, Ranking.RankingType rankingType) {

        List<? extends IRanking> lastRankings;
        List<? extends IRanking> beforeRankings;
        if(Ranking.RankingType.best_posting.equals(rankingType)) {
            lastRankings = (last != null) ? postingRankingRepository.findAllByRankingTypeAndDate(rankingType, last) : Collections.emptyList();
            beforeRankings = (before != null) ? postingRankingRepository.findAllByRankingTypeAndDate(rankingType, before) : Collections.emptyList();
        } else if(Ranking.RankingType.best_keyword.equals(rankingType)) {
            lastRankings = (last != null) ? keywordRankingRepository.findAllByRankingTypeAndDate(rankingType, last) : Collections.emptyList();
            beforeRankings = (before != null) ? keywordRankingRepository.findAllByRankingTypeAndDate(rankingType, before) : Collections.emptyList();
        } else if(Ranking.RankingType.best_user_by_clinic.equals(rankingType) || Ranking.RankingType.best_user_by_question.equals(rankingType)) {
            lastRankings = (last != null) ? userRankingRepository.findAllByRankingTypeAndDate(rankingType, last) : Collections.emptyList();
            beforeRankings = (before != null) ? userRankingRepository.findAllByRankingTypeAndDate(rankingType, before) : Collections.emptyList();
        } else if(Ranking.RankingType.best_goods.equals(rankingType)) {

            List<GoodsRanking> goodsRankings = goodsRankingRepository.findList(rankingType, last);
            if(!goodsRankings.isEmpty()) {
                List<Goods> goodsList = goodsRepository.findAllById(goodsRankings.get(0).getItems().stream().map(GoodsRanking.GoodsRankingDetail::getGoodsCd).collect(Collectors.toList()));
                lastRankings = goodsRankings.get(0).getItems().stream().peek(ranking -> {
                    Optional<Goods> optionalGoods = goodsList.stream().filter(goods -> goods.getGoodsNo().equals(ranking.getKey())).findFirst();
                    optionalGoods.ifPresent(ranking::setGoods);
                }).filter(ranking -> ranking.getGoods() != null).collect(Collectors.toList());

            } else {
                lastRankings = Collections.emptyList();
            }
            beforeRankings = Collections.emptyList();
        } else {
            lastRankings = Collections.emptyList();
            beforeRankings = Collections.emptyList();
        }

        return compare(beforeRankings, lastRankings).stream().limit(10).collect(Collectors.toList());
    }



    /**
     * ranking order
     * */
    @Transactional
    public void updateRankingOrder(List<String> genres){

        int orderCnt = 1;
        for(String genre : genres){
            if(genre.isEmpty() || !genreList.contains(genre))
                continue;
            RankingOrder rankingOrder = rankingOrderRepository.findByRankingGenre(genre).orElse(null);
            if(rankingOrder==null) {
                RankingOrder rankingOrder1 = new RankingOrder();
                rankingOrder1.setRankingGenre(genre);
                rankingOrder1.setOrderCount(orderCnt++);
                rankingOrderRepository.save(rankingOrder1);
            } else {
                rankingOrder.setOrderCount(orderCnt++);
            }
        }

        List<RankingOrder> rankingOrderList = rankingOrderRepository.findAll();
        for(RankingOrder rankingOrder : rankingOrderList){
            if( genres.contains(rankingOrder.getRankingGenre()) )
                continue;

            rankingOrderRepository.delete(rankingOrder);
        }

    }

    @Cacheable(value = "ranking_order", key = "'rankingOrder'")
    public List<String> getRankingOrder(){

        List<RankingOrder> rankingOrders = rankingOrderRepository.findAll();
        rankingOrders.sort(Comparator.comparingInt(RankingOrder::getOrderCount));

        return rankingOrders.stream()
                .map(RankingOrder::getRankingGenre)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDto> findUserRanking(ZonedDateTime date, Ranking.RankingType rankingType) {
        Optional<RankingLog> lastRankingLog = rankingLogRepository.findLastByDate(date);
        return lastRankingLog.map(log -> userRankingRepository.findAllByRankingTypeAndDate(rankingType, log.getDate(), PageRequest.of(0, 4)).stream().map(UserRanking::getUser).map(UserDto::of).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Transactional(readOnly = true)
    public List<PostingDto> findPostingRanking(ZonedDateTime date, Ranking.RankingType rankingType) {
        Optional<RankingLog> lastRankingLog = rankingLogRepository.findLastByDate(date);
        return lastRankingLog.map(log -> postingRankingRepository.findAllByRankingTypeAndDate(rankingType, log.getDate(), PageRequest.of(0, 3)).stream().map(PostingRanking::getPosting).map(PostingDto::of).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Transactional
    public void removePostingRankingByPhoto(Photo photo) {
        List<PostingRanking> postingRankings = postingRankingRepository.findPostingRankingByPosting(photo);
        for(PostingRanking postingRanking : postingRankings){
            postingRankingRepository.delete(postingRanking);
        }
    }

}
