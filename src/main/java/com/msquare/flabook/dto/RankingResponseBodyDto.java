package com.msquare.flabook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingResponseBodyDto implements Serializable {

    @Data
    @AllArgsConstructor
    public static class Keyword implements Serializable {
        private List<RankingDto> popularity;
    }

    @Data
    @AllArgsConstructor
    public static class User implements Serializable {
        private List<RankingDto> question;
        private List<RankingDto> clinic;
    }


    private String date;

    private Keyword keyword;

    private User user;

    private List<RankingDto> posting;
    private List<RankingDto> goods;

    @Builder.Default
    private ZonedDateTime updated = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Asia/Seoul"));

    public RankingResponseBodyDto(String date, List<RankingDto> popularKeywordRanking, List<RankingDto> questionUserRanking, List<RankingDto> clinicUserRanking, List<RankingDto> postingRanking , List<RankingDto> goodsRanking ) {
        this.date = date;
        this.keyword = new Keyword(popularKeywordRanking);
        this.user = new User(questionUserRanking, clinicUserRanking);
        this.posting = postingRanking;
        this.goods = goodsRanking;
        this.updated = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Asia/Seoul"));
    }


}
