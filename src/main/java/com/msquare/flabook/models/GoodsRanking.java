package com.msquare.flabook.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import com.msquare.flabook.models.converter.GoodsRankingDetailListConverter;
import com.msquare.flabook.models.shop.Goods;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class GoodsRanking extends BaseEntity {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoodsRankingDetail implements IRanking, Serializable {

        private String goodsCd;
        private long score;

        @JsonIgnore
        private Goods goods;

        private Ranking.RankingType rankingType;

        @Override
        public String getKey() { //NOSONAR
            return goodsCd;
        }

        @Override
        public Goods getTarget() { //NOSONAR
            return goods;
        }

        @JsonIgnore
        private String key;
        @JsonIgnore
        private Goods target;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = GoodsRankingDetailListConverter.class)
    private List<GoodsRankingDetail> items;

    private ZonedDateTime start;
    private ZonedDateTime end;

    @Builder.Default
    @Column(columnDefinition = "bit default 0")
    private boolean active = false;

    @NonNull
    @Column(length = 25)
    @Enumerated(EnumType.STRING)
    private Ranking.RankingType rankingType;

}
