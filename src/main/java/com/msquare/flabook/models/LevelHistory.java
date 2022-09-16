package com.msquare.flabook.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class LevelHistory {

    public enum HistoryType{levelup , tenthausand} //NOSONAR

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    @NonNull
    @Column(columnDefinition="TINYINT")
    public Integer level = 1;

    @NonNull
    @Column(name="history_type", columnDefinition="TINYINT")
    public HistoryType historyType;

    @NonNull
    @ManyToOne(fetch=FetchType.LAZY)
    public User user;

    @NonNull
    public ZonedDateTime dt;

    @NonNull
    @Column(name="comment_count")
    public Integer commentCount = 0;

}
