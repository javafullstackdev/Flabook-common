package com.msquare.flabook.models;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(name = "UK_LIKE", columnNames = {"user_id", "comment_id"})})
public class LikeComment implements IUserCommentActivity, INotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Embedded
    private UserCommentRelation relation;

    private ZonedDateTime createdAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = ZonedDateTime.now();
    }

    @Override
    public String getText() {
        return relation.getComment().getText();
    }

    @Override
    public User getOwner() {
        return relation.getUser();
    }

    @Override
    public ImageResource getThumbnail() {
        return relation.getComment().getThumbnail();
    }

    @Override
    public Resource asResource() {
        return new Resource(getId(), Resource.ResourceType.likecomment, relation.getComment().getPosting().getId(), relation.getComment().getPosting().asResource().getResourceType());
    }
}
