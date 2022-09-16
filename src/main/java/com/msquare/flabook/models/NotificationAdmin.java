package com.msquare.flabook.models;

import java.time.ZonedDateTime;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import com.msquare.flabook.enumeration.ExpertGroup;
import com.msquare.flabook.enumeration.OsType;
import org.hibernate.search.annotations.IndexedEmbedded;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class NotificationAdmin extends BaseEntity implements INotification {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "NONE"))
    @IndexedEmbedded(includePaths = {"id"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("posting")
    private User user;

    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "NONE"))
    @IndexedEmbedded(includePaths = {"id"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"comments", "attachments"})
    private Posting posting;


    @BatchSize(size = 50)
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    private String title;

    @Override
    public String getTitle() {
    	return this.title;
    }

    private String text;

    private String link;

    // 예약발송여부
    @Builder.Default
    @Column(columnDefinition = "bit default 1")
    private boolean isReserved = true;

    // 예약시간
    private ZonedDateTime reservedTime;

    // 발송시간
    private ZonedDateTime sendTime;

    // 발송대상-OsType
    @Column(columnDefinition = "smallint default 0")
    @Enumerated(EnumType.ORDINAL)
    private OsType deviceGroup;

    @Override
    public OsType getOsType() {
        return deviceGroup;
    }

    // 발송대상-전문가 그룹
    @Column(columnDefinition = "smallint default 0")
    @Enumerated(EnumType.ORDINAL)
    private ExpertGroup targetGroup;

    private ImageResource thumbnail;

	@Override
    public ImageResource getThumbnail() {
        return this.thumbnail;
    }

	@Override
	public Resource asResource() {
		if(this.getLink() != null && !this.getLink().isEmpty()) {
		    return new Resource(this.getLink(), Resource.ResourceType.web, this.getLink(),  Resource.ResourceType.web);
        } else {
            return this.getPosting().asResource();
        }
	}
}
