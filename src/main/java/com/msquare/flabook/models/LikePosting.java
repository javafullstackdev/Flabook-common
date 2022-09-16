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
@Table(uniqueConstraints = {@UniqueConstraint(name = "UK_LIKE", columnNames = {"user_id", "posting_id"})})
public class LikePosting implements IUserPostingActivity, INotification, IActivity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Embedded
    private UserPostingRelation relation;

    @NonNull
    @Column(columnDefinition = "tinyint")
    @Enumerated(EnumType.ORDINAL)
    private PostingType postingType;

    private ZonedDateTime createdAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = ZonedDateTime.now();
    }

    @Override
    public String getText() {
        return relation.getPosting().getText();
    }

    @Override
    public User getOwner() {
        return relation.getUser();
    }

    @Override
    public ImageResource getThumbnail() {
        return relation.getPosting().getThumbnail();
    }

    @Override
    public Resource asResource() {
        return new Resource(getId(), Resource.ResourceType.likeposting, relation.getPosting().getId(), relation.getPosting().asResource().getResourceType());
    }


}
