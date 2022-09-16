package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.common.configurations.ServiceHost;
import com.msquare.flabook.dto.mapper.UserMapper;
import com.msquare.flabook.enumeration.UserRole;
import com.msquare.flabook.enumeration.UserStatus;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.ImageResource;
import com.msquare.flabook.models.User;
import com.msquare.flabook.models.UserActivity;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView({Views.BaseView.class, Views.WebAdminJsonView.class})
//@JsonIgnoreProperties(ignoreUnknown = true, value = {"createdAt", "modifiedAt", "level", "status"})
public class UserDto extends UserIdWithNicknameDto {

    private long id;
    private String nickname;

    private int level;
    private UserRole role;

    private UserStatus status;
    private LevelContainer.LevelInfo levelInfo = null;

    public LevelContainer.LevelInfo getLevelInfo() {
        if (levelInfo != null)
            return levelInfo;

        return LevelContainer.getInstance().findLevelInfo(level);
    }

    public void setLevelInfo(LevelContainer.LevelInfo levelInfo) {
        this.levelInfo = levelInfo;
    }

    @JsonView({Views.UserProfileDetailJsonView.class, Views.WebAdminJsonView.class})
    private UserActivity activity;

    @JsonIgnore
    private ImageResource imageResource;

    private String photoUrl = null;

    @JsonView({Views.MyProfileDetailJsonView.class, Views.WebAdminJsonView.class})
    private String provider;

    @Override
    public String getPhotoUrl() {
        if (photoUrl != null)
            return photoUrl;

        if(imageResource == null)
            return null;

        return ServiceHost.getS3Url( imageResource.getFilekey());
    }

    public void setPhotoUrl(String url) {
        this.photoUrl = url;
    }

    @JsonView(Views.WebAdminJsonView.class)
    private ZonedDateTime createdAt;

    public static UserDto of(User user) {
        return UserMapper.INSTANCE.of(user);
    }

    private int badgeCount;

    private BadgeDto representBadge;

    @SuppressWarnings("unused")
    @JsonProperty("newbie")
    public Boolean isNewbie() {
        if(this.activity == null)
            return false;

        return this.activity.getPostingCount() <= 2;
    }

    private int totalPhotosCnt;
    private int totalPhotosLikeCnt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean photoEnable;

    public static UserDto ofWithPhotoProperty(User user) {
        return UserMapper.INSTANCE.ofWithPhotoProperty(user);
    }
}
