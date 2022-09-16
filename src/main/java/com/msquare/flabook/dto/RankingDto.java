package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.mapper.RankingMapper;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.models.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingDto implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String text;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PostingType postingType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer readCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer commentCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer likeCount;

    private String thumbnail;

    private int rank;
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnore
    private UserDto user;
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnore
    private PostingDto posting;
    //@JsonInclude(JsonInclude.Include.NON_NULL)

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String keyword;

    private Integer fluctuation;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal price;

    public static RankingDto of(UserRanking ranking) {
        return RankingMapper.INSTANCE.of(ranking);
    }


    public static RankingDto of(PostingRanking ranking) {
        return RankingMapper.INSTANCE.of(ranking);
    }

    public static RankingDto of(KeywordRanking ranking) {
        return RankingMapper.INSTANCE.of(ranking);
    }

    public static RankingDto of(GoodsRanking.GoodsRankingDetail ranking) {
        return RankingMapper.INSTANCE.of(ranking);
    }

    public static RankingDto of(IRanking ranking) {
        if(ranking instanceof UserRanking) {
            return RankingDto.of((UserRanking) ranking);
        } else if(ranking instanceof PostingRanking) {
            return RankingDto.of((PostingRanking) ranking);
        } else if(ranking instanceof KeywordRanking) {
            return RankingDto.of((KeywordRanking) ranking);
        } else if(ranking instanceof GoodsRanking.GoodsRankingDetail) {
            return RankingDto.of((GoodsRanking.GoodsRankingDetail) ranking);
        }  else {
            return null;
        }
    }

    public static RankingDto of(RankingDto before, RankingDto after) {
        return RankingMapper.INSTANCE.of(before, after);
    }

}
