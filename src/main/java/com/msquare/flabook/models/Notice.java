package com.msquare.flabook.models;

import lombok.Data;
import com.msquare.flabook.enumeration.NoticeStatus;
import com.msquare.flabook.enumeration.NoticeType;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.ZonedDateTime;

//공지사항
@Data
@Entity
@DynamicUpdate
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 8192 ,columnDefinition = "TEXT")
    private String description;

    private String url;

    private ImageResource imageResource;

    //노출시작시간
    private ZonedDateTime start;

    //노출종료시간
    private ZonedDateTime end;

    @Column(name = "interval_days")
    private int interval;

    @Column(columnDefinition = "bit default 0")
    private boolean popup;

    @Column(columnDefinition = "tinyint")
    private NoticeStatus status;

    @Column(columnDefinition = "tinyint comment '0 : notice, 1 : event'")
    private NoticeType type;
}
