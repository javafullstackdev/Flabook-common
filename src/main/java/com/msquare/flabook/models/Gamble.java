package com.msquare.flabook.models;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(callSuper=false)
@ToString(of={"id", "title", "maxAttempt", "retryHour"})
@Entity
public class Gamble extends BaseEntity implements INotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(255) default '' comment '제목'")
    private String title;

    @Builder.Default
    @Column(columnDefinition = "bit default 0 comment '활성여부'")
    private boolean active = false;

    @OneToMany(mappedBy = "gamble", orphanRemoval = true, cascade = {CascadeType.ALL})
    private List<GambleItem> items;

    @OneToMany(mappedBy = "gamble", orphanRemoval = true)
    private List<GambleResult> results;

    @Column(columnDefinition = "int default 1 comment '시도횟수'")
    private int maxAttempt;

    @Column(columnDefinition = "int default 2 comment '재시도가능시간'")
    private int retryHour;

    @Column(columnDefinition = "datetime comment '초기화시간'")
    private ZonedDateTime initDate;

    @Builder.Default
    @Column(columnDefinition = "tinyint default 0 comment '초기화 기준시(24시간 기준)'")
    private int initHour = 0;

    @Column(columnDefinition = "datetime comment '시작일'")
    private ZonedDateTime startDate;

    @Column(columnDefinition = "datetime comment '종료일'")
    private ZonedDateTime endDate;

    @Column(columnDefinition = "varchar(255) default '' comment '이벤트페이지주소'")
    private String url;

    @Column(columnDefinition = "int default 0 comment 'gamble version 확인용'")
    private int version;

    @Embedded
    private ImageResource resource;

    @Override
    public Resource asResource() {
        //ServiceHost.getUrl()
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.url);
        builder.queryParam("auth", true);
        String buildUrl = builder.toUriString();

        return new Resource(buildUrl, Resource.ResourceType.web, buildUrl, Resource.ResourceType.web);
    }

    @Override
    public String getText() {
        return this.title;
    }

    @Override
    public User getOwner() {
        return null;
    }

    @Override
    public ImageResource getThumbnail() {
        return resource;
    }

}
