package com.msquare.flabook.models;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.ZonedDateTime;

@EqualsAndHashCode
@Data
@Entity
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
public class SearchQueryHistory {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private ZonedDateTime dt;

    @NonNull
    @Column(length = 60)
    private String keyword;

    private long count;

    public SearchQueryHistory(@NonNull ZonedDateTime dt, @NonNull String keyword, long count) {
        this.dt = dt;
        this.keyword = keyword;
        this.count = count;
    }

    public void increment(long count) {
        this.count += count;
    }
}
