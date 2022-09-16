package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.msquare.flabook.dto.mapper.NotificationMapper;
import com.msquare.flabook.enumeration.EventType;
import com.msquare.flabook.enumeration.OsType;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.ImageResource;
import com.msquare.flabook.models.Notification;
import com.msquare.flabook.common.configurations.ServiceHost;

import javax.persistence.Tuple;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.BaseView.class)
@JsonIgnoreProperties(value = "thumbnail", ignoreUnknown = true)
public class NotificationDto implements Serializable {

    private long id;

    private String title;

    private String description;

    private ImageResource thumbnail;

    private EventType eventType;

    private String photoUrl = null;

    private UserDto owner;
    private Boolean unread = true;

    private ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;

    /**
     * 수신 디바이스
     */
    @Accessors(chain = true)
    private OsType osType;

    public String getPhotoUrl() {
        if (photoUrl != null)
            return photoUrl;

        if(thumbnail != null) {
            if(thumbnail.getFilekey().indexOf("http") == 0) {
                return thumbnail.getFilekey();
            } else {
                return  ServiceHost.getS3Url(thumbnail.getFilekey() + "?d=120x120");
            }
        } else {
            return null;
        }
    }

    private ResourceDto resource;

    public static NotificationDto of(Notification notification) {
        return NotificationMapper.INSTANCE.of(notification);
    }

    public static NotificationDto of(Tuple tuple) {
        NotificationDto notificationDto = NotificationMapper.INSTANCE.of((Notification) tuple.get(0));
        if(tuple.get(1) != null) {
            notificationDto.unread((boolean)tuple.get(1));
        } else {
            notificationDto.unread(true);
        }

        return notificationDto;
    }

    private void unread(boolean b) {
        this.unread = b;
    }

}
