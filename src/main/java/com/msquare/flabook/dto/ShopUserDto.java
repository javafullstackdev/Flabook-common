package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.mapper.ShopUserMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.User;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.BaseView.class)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"createdAt", "modifiedAt", "level", "status", "providerId"})
public class ShopUserDto extends UserDto {

    private static final String PROVIDER_PHONE = "phone";
    private static final String PROVIDER_FACEBOOK = "facebook";
    private static final String PROVIDER_NAVER = "naver";
    private static final String PROVIDER_KAKAO = "kakao";
    private static final String PROVIDER_EMAIL = "email";
    private static final String PROVIDER_APPLE = "apple";

    private String providerId;

    //고정 쇼핑몰ID
    private String shopUserId;

    public String getShopUserId() {
        if(shopUserId != null)
            return shopUserId;

        if(PROVIDER_APPLE.equals(super.getProvider())) {
            shopUserId = String.format("apple_%d", super.getId());
        } else {
            shopUserId = String.format("%s_%d", this.providerId, super.getId());
        }
        return shopUserId;
    }

    public static ShopUserDto of(User user) {
        return ShopUserMapper.INSTANCE.of(user);
    }


}
