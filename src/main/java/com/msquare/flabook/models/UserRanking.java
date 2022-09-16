package com.msquare.flabook.models;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class UserRanking extends Ranking<User> {

    @SuppressWarnings("unused")
    @Builder
    public UserRanking(@NonNull RankingType rankingType, long score, ZonedDateTime date, User user) {
        super(rankingType, score, date);
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Override
    public String getKey() {
        return String.valueOf(user.getId());
    }

    @Override
    public User getTarget() {
        return this.user;
    }
}
