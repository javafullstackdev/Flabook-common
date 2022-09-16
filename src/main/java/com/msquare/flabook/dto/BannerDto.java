package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.msquare.flabook.common.configurations.ServiceHost;
import com.msquare.flabook.dto.mapper.BannerMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.Banner;
import com.msquare.flabook.models.ImageResource;

import java.io.Serializable;

@Data
@JsonView(Views.BaseView.class)
@NoArgsConstructor
@AllArgsConstructor
public class BannerDto implements Serializable {

    private String title;
    private ResourceDto resource;
    @JsonIgnore
    private ImageResource imageResource;

    private String photoUrl;

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


    public static BannerDto of(Banner banner) {
        return BannerMapper.INSTANCE.of(banner);
    }
}
