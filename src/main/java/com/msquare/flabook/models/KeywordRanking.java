package com.msquare.flabook.models;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class KeywordRanking extends Ranking<String> {

    @Builder
    public KeywordRanking(@NonNull RankingType rankingType, long score, @NonNull ZonedDateTime date, String keyword) {
        super(rankingType, score, date);
        this.keyword = keyword;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64)
    private String keyword;

    @Override
    public String getKey() {
        return this.keyword;
    }

    @Override
    public String getTarget() {
        return this.keyword;
    }
}
