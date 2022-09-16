package com.msquare.flabook.models;

import lombok.*;
import lombok.experimental.Accessors;
import com.msquare.flabook.enumeration.PostingType;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(name = "UK_WATCH", columnNames = {"user_id", "posting_id"})})
public class WatchPosting implements IUserPostingActivity {

    @SuppressWarnings("unused")
    public WatchPosting(@NonNull PostingType postingType, @NonNull UserPostingRelation relation, boolean enable) {
        this.postingType = postingType;
        this.relation = relation;
        this.enable = enable;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(columnDefinition = "tinyint")
    @Enumerated(EnumType.ORDINAL)
    private PostingType postingType;

    @NonNull
    @Embedded
    private UserPostingRelation relation;

    private ZonedDateTime createdAt;

    @Accessors(chain = true)
    @Column(columnDefinition = "bit default 1")
    private boolean enable = true;

    @PrePersist
    private void prePersist() {
        this.createdAt = ZonedDateTime.now();
    }

}
