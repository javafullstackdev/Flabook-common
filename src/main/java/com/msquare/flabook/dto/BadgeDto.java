package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.msquare.flabook.common.configurations.ServiceHost;
import com.msquare.flabook.dto.mapper.BadgeMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.Badge;
import com.msquare.flabook.models.ImageResource;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonView({Views.MyProfileDetailJsonView.class, Views.UserProfileDetailJsonView.class})
public class BadgeDto implements Serializable {

    private long id;
    private String title;
    private String description1;

    private String description2;

    @JsonIgnore
    private Boolean active;

    @JsonIgnore
    private int orderCount;

    @JsonIgnore
    private ImageResource trueImageResource;

    @JsonIgnore
    private ImageResource falseImageResource;

    @JsonIgnore
    private String trueImageUrl = null;

    @JsonIgnore
    private String falseImageUrl = null;

    private Boolean representable ;

    private Boolean currentRepresent ;

    private String imageUrl;

    public String getImageUrl() {
        if (StringUtils.isNotBlank(this.imageUrl))
            return this.imageUrl;
        return Boolean.FALSE.equals(representable) ?  getFalseImageUrl() : getTrueImageUrl();
    }

    @JsonIgnore
    public String getTrueImageUrl() {
        if (trueImageUrl != null)
            return trueImageUrl;

        if(trueImageResource == null)
            return null;

        return ServiceHost.getS3Url(trueImageResource.getFilekey());
    }

    @JsonIgnore
    public String getFalseImageUrl() {
        if (falseImageUrl != null)
            return falseImageUrl;

        if(falseImageResource == null)
            return null;

        return ServiceHost.getS3Url(falseImageResource.getFilekey());
    }

    public void setTrueImageUrl(String url) {
        this.trueImageUrl = url;
    }
    public void setFalseImageUrl(String url) {
        this.falseImageUrl = url;
    }
    public static BadgeDto of(Badge badge) {
        return BadgeMapper.INSTANCE.of(badge);
    }

}
