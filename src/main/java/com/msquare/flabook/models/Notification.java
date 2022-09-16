package com.msquare.flabook.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import com.msquare.flabook.dto.CommentDto;
import com.msquare.flabook.enumeration.EventType;
import com.msquare.flabook.util.CommonUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"text", "owner", "resource"})
@Entity
@ToString
@NoArgsConstructor
@DynamicUpdate
public class Notification extends BaseEntity implements Serializable, INotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 55)
    private String text;

    @BatchSize(size = 50)
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    /**
     * {사용자 ID -> 읽음여부}
     */
    @ElementCollection
    @MapKeyColumn(name = "user_id")
    @Column(name="unread", columnDefinition = "bit default 1")
    @CollectionTable(name = "NotificationRecipient"
            , joinColumns = @JoinColumn(name = "notification_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
            , indexes = {@Index(name = "IDX_USER", columnList = "user_id")}
    )
    //@OneToMany(fetch = FetchType.LAZY)
    private Map<Long, Boolean> recipients;

    @AttributeOverride(name = "dimension", column = @Column(name = "dimension"))
    private ImageResource thumbnail;

    @NonNull
    @AttributeOverride(name = "resourceId", column = @Column(name = "resourceId"))
    @AttributeOverride(name = "resourceType", column = @Column(name = "resourceType"))
    @AttributeOverride(name = "referenceId", column = @Column(name = "referenceId"))
    @AttributeOverride(name = "referenceType", column = @Column(name = "referenceType"))
    @Embedded
    private Resource resource;

    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(20)")
    private EventType eventType;

    @Column(length = 55)
    private String title;

    @Override
    public String getTitle() {

    	if(this.title != null) {
    		return this.title;
    	}

        String notiTitle = (owner != null) ? owner.getNickname() + "님이 " : "";
        switch (eventType) {
            case NEW_QUESTION:
            case NEW_BOAST:
            case NEW_CLINIC:
            case NEW_GUIDE:
            case NEW_MAGAZINE:
                notiTitle += "새 글을 등록했습니다.";
                break;
            case NEW_COMMENT:
                notiTitle += "새 댓글을 남겼습니다.";
                break;
            case NEW_REPLY:
                notiTitle += "새 답글을 남겼습니다.";
                break;
            case NEW_MENTION:
                notiTitle += "회원님을 언급했습니다.";
                break;

            case NEW_LIKE_POSTING:
                notiTitle += "회원님의 게시글을 좋아합니다.";
                break;
            case NEW_LIKE_COMMENT:
                notiTitle += "회원님의 답변을 좋아합니다.";
                break;
            case NEW_AD:
                notiTitle = "[광고]";
                break;
            case NEW_SHOP:
                notiTitle = "모야모샵";
                break;
            default:
                notiTitle = "관심을 보였습니다.";
                break;
        }

        return notiTitle;
    }

    public String getDescription() {
        String description = "";
        switch (this.resource.getReferenceType()) {
            case advertisement:
                break;
            case notice:
            case shop:
            default:
                description = this.text;
                break;
        }

        return description;
    }

    public Notification(INotification notification, EventType eventType) {
        if(notification instanceof Comment) {
            //언급된 사용자
            Comment comment = (Comment)notification;
            this.text = CommonUtils.convertDotString(CommentDto.of(comment).getText(), 50);
        } else {
            this.text = CommonUtils.convertDotString(notification.getText(), 50);
        }

        this.title = CommonUtils.convertDotString(notification.getTitle(), 50);

        this.owner = notification.getOwner();
        this.thumbnail = notification.getThumbnail();
        this.resource = notification.asResource();
        this.eventType = eventType;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public User getOwner() {
        return this.owner;
    }

    @Override
    public ImageResource getThumbnail() {
        return this.thumbnail;
    }

    @Override
    public Resource asResource() {
        return this.resource;
    }
}
