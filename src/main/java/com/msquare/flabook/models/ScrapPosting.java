package com.msquare.flabook.models;

import lombok.*;
import com.msquare.flabook.enumeration.PostingType;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(name = "UK_SCRAP", columnNames = {"user_id", "posting_id"})})
public class ScrapPosting implements IUserPostingActivity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Embedded
    private UserPostingRelation relation;

    private ZonedDateTime createdAt;

    @NonNull
    @Column(columnDefinition = "tinyint")
    private PostingType postingType;

    @PrePersist
    private void prePersist() {
        this.createdAt = ZonedDateTime.now();
    }

}
