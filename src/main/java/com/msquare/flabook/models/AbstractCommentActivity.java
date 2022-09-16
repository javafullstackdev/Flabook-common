package com.msquare.flabook.models;

import lombok.*;
import com.msquare.flabook.dto.CommentDto;
import com.msquare.flabook.enumeration.OsType;
import com.msquare.flabook.enumeration.PostingType;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@AssociationOverride(name="source.user", joinColumns = @JoinColumn(name="source_user_id"))
@AssociationOverride(name="source.comment", joinColumns = @JoinColumn(name="source_comment_id"))
@AssociationOverride(name="target.user", joinColumns = @JoinColumn(name="target_user_id"))
@AssociationOverride(name="target.comment", joinColumns = @JoinColumn(name="target_comment_id"))
public abstract class AbstractCommentActivity extends BaseEntity implements IUserPostingActivity, INotification, IActivity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private UserPostingRelation relation;

    @NonNull
    private UserCommentRelation source;

//    @NonNull
//    @Embedded
//    @AttributeOverride(name = "comment", column = @Column(name = "target_comment_id", insertable = false, updatable = false))
//    @AttributeOverride(name = "user", column = @Column(name= "target_user_id", insertable = false, updatable = false))
    @NonNull
    private UserCommentRelation target;

    @Override
    public String getText() {
        return CommentDto.of(getSource().getComment()).getText();
    }

    @Override
    public User getOwner() {
        return source.getUser();
    }

    @Override
    public ImageResource getThumbnail() {
        return source.getComment().getPosting().getThumbnail();
    }

    @Override
    public Resource asResource() {
        return source.getComment().asResource();
    }

    @Override
    public OsType getOsType() {
        return OsType.all;
    }

    @Override
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(25)")
    public abstract PostingType getPostingType() ;
}
