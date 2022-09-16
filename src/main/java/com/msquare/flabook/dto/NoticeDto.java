package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.common.configurations.ServiceHost;
import com.msquare.flabook.dto.mapper.NoticeMapper;
import com.msquare.flabook.enumeration.NoticeStatus;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.ImageResource;
import com.msquare.flabook.models.Notice;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.BaseView.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoticeDto implements Serializable {

    private long id;

    private String title;
    private String description;

    //노출시작시간
    private ZonedDateTime start;

    //노출종료시간
    private ZonedDateTime end;

    @JsonIgnore
    private ImageResource imageResource;

    private int interval;
    private NoticeStatus status;
    private String url;
    private boolean popup;

    private String photoUrl = null;

    public String getPhotoUrl() {
        if (photoUrl != null)
            return photoUrl;

        if(imageResource == null)
            return null;

        return ServiceHost.getS3Url(imageResource.getFilekey());
    }

    public void setPhotoUrl(String url) {
        this.photoUrl = url;
    }

    public static NoticeDto of(Notice notice) {
        return NoticeMapper.INSTANCE.of(notice);
    }

}
