package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.common.configurations.ServiceHost;
import com.msquare.flabook.dto.mapper.UserIdWithNicknameMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.ImageResource;
import com.msquare.flabook.models.User;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView({Views.BaseView.class})
public class UserIdWithNicknameDto implements Serializable {

    private long id;
    private String nickname;
    @JsonIgnore
    private ImageResource imageResource;

    private String photoUrl = null;

    public String getPhotoUrl() {
        if (photoUrl != null)
            return photoUrl;

        if(imageResource == null)
            return null;

        return ServiceHost.getS3Url( imageResource.getFilekey());
    }

    public static UserIdWithNicknameDto of(User user) {
        return UserIdWithNicknameMapper.INSTANCE.of(user);
    }
}
