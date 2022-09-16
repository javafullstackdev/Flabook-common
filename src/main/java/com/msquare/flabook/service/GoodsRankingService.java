package com.msquare.flabook.service;

import lombok.RequiredArgsConstructor;
import com.msquare.flabook.models.GoodsRanking;
import com.msquare.flabook.models.Ranking;
import com.msquare.flabook.repository.GoodsRankingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class GoodsRankingService {

    private final GoodsRankingRepository goodsRankingRepository;

    public List<GoodsRanking> getAllBestGoods() {
        return goodsRankingRepository.findAll();
    }

    public void registBestGoods(String[] titles, ZonedDateTime startDate, ZonedDateTime endDate, String status) {
        GoodsRanking goodsRanking = new GoodsRanking();
        goodsRanking.setStart(startDate);
        goodsRanking.setEnd(endDate);
        goodsRanking.setRankingType(Ranking.RankingType.best_goods);

        List<GoodsRanking.GoodsRankingDetail> items = new ArrayList<>();
        for(int i=0;i< titles.length;i++){
            GoodsRanking.GoodsRankingDetail goodsRankingDetail = new GoodsRanking.GoodsRankingDetail();
            goodsRankingDetail.setGoodsCd(titles[i]);
            goodsRankingDetail.setKey(titles[i]);
            goodsRankingDetail.setScore(50L - i * 20L);
            items.add(goodsRankingDetail);
        }

        goodsRanking.setItems(items);
        goodsRanking.setActive(status.equals("open"));

        goodsRankingRepository.save(goodsRanking);
    }

    public void removeBestGoods(Long bestGoodsId) {
        goodsRankingRepository.deleteById(bestGoodsId);
    }

    public void modifyBestGoods(Long bestGoodsId, String[] codes, ZonedDateTime startDate, ZonedDateTime endDate, String status) {
        GoodsRanking goodsRanking = goodsRankingRepository.findById(bestGoodsId).orElse(null);
        if(Objects.isNull(goodsRanking)) return;
        goodsRanking.setStart(startDate);
        goodsRanking.setEnd(endDate);

        List<GoodsRanking.GoodsRankingDetail> items = new ArrayList<>();
        for(int i=0;i< codes.length;i++){
            GoodsRanking.GoodsRankingDetail goodsRankingDetail = new GoodsRanking.GoodsRankingDetail();
            goodsRankingDetail.setGoodsCd(codes[i]);
            goodsRankingDetail.setKey(codes[i]);
            goodsRankingDetail.setScore(50L - i * 20L);
            items.add(goodsRankingDetail);
        }

        goodsRanking.setItems(items);
        goodsRanking.setActive(status.equals("open"));

        goodsRankingRepository.save(goodsRanking);
    }

    public List<GoodsRanking> getAllBestGoodsByRankingType(Ranking.RankingType rankingType, int start, int length) {
        return goodsRankingRepository.findAllByRankTypeWithPageable(rankingType, PageRequest.of(start/length, length, Sort.by(Sort.Direction.DESC, "id")));
    }

    public Integer getAllBestGoodsCountByRankingType(Ranking.RankingType rankingType) {
        return goodsRankingRepository.findAllByRankType(rankingType).size();
    }
}
