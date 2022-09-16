package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.mapper.UserWithSecurityMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.User;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView({Views.BaseView.class, Views.WebAdminJsonView.class})
public class UserWithSecurityDto extends UserDto {

    @JsonView({Views.MyProfileDetailJsonView.class, Views.WebAdminJsonView.class})
    private UserSecurityDto security;

    @JsonView({Views.MyProfileDetailJsonView.class, Views.WebAdminJsonView.class})
    private String provider;

    public static UserWithSecurityDto of(User user) {
        return UserWithSecurityMapper.INSTANCE.of(user);
    }

    public static UserWithSecurityDto ofPhotoEnable(User user) {
        return UserWithSecurityMapper.INSTANCE.ofWithPhotoProperty(user);
    }
}
