package com.msquare.flabook.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

import org.mapstruct.Mapper;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import com.msquare.flabook.common.configurations.ServiceHost;
import com.msquare.flabook.dto.mapper.NotificationAdminMapper;
import com.msquare.flabook.enumeration.ExpertGroup;
import com.msquare.flabook.enumeration.OsType;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.ImageResource;
import com.msquare.flabook.models.NotificationAdmin;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@RequiredArgsConstructor
@JsonView(Views.WebAdminJsonView.class)
@Mapper
public class NotificationAdminDto implements Serializable {

	private Long id;

	private UserDto owner;

	private String title;

	private String text;

	private String link;

	private boolean isReserved;

	// 예약시간
    private ZonedDateTime reservedTime;

    // 발송테스트시간
    private ZonedDateTime testTime;

    // 발송시간
    private ZonedDateTime sendTime;

    private boolean isSendTest;

    private OsType deviceGroup;

    private ExpertGroup targetGroup;

    private UserDto user;
    private PostingDto posting;

    private ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;

    private ImageResource thumbnail;

    private String photoUrl = null;

    public String getPhotoUrl() {
        if (photoUrl != null)
            return photoUrl;

        if(thumbnail == null)
            return null;

        return ServiceHost.getS3Url(thumbnail.getFilekey());
    }

    public static NotificationAdminDto of(NotificationAdmin notification) {
        return NotificationAdminMapper.INSTANCE.of(notification);
    }

}
