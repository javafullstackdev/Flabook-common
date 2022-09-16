package com.msquare.flabook.models;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class PostingRanking extends Ranking<Posting> {

    @SuppressWarnings(value = "unused")
    @Builder
    public PostingRanking(@NonNull RankingType rankingType, long score, @NonNull ZonedDateTime date, Posting posting) {
        super(rankingType, score, date);
        this.posting = posting;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Posting posting;

    public PostingRanking() {
        super();
    }

    @Override
    public String getKey() {
        return String.valueOf(posting.getId());
    }

    @Override
    public Posting getTarget() {
        return this.posting;
    }
}
