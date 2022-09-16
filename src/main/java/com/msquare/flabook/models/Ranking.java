package com.msquare.flabook.models;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@SuppressWarnings("unused")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Data
@MappedSuperclass
public abstract class Ranking<T> extends BaseEntity implements IRanking {

    @SuppressWarnings("java:S115")
    public enum RankingType {
        best_keyword("인기질문"),
        best_posting("인기 게시물(자랑하기,자유수다)"),
        best_user_by_question("우수회원(이름이모야)"),
        best_user_by_clinic("우수회원(식물클리닉)"),
        best_goods("인기상품"),
        best_photo("인기포토"),
        best_user_by_photo("우수회원(포토)");

        final String name;
        RankingType(String name) {
            this.name = name;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(length = 25)
    @Enumerated(EnumType.STRING)
    private RankingType rankingType;

    private long score;

    @NonNull
    private ZonedDateTime date;

    protected Ranking(@NonNull RankingType rankingType, long score, @NonNull ZonedDateTime date) {
        this.rankingType = rankingType;
        this.score = score;
        this.date = date;
    }
}
