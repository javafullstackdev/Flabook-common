package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.common.configurations.ServiceHost;
import com.msquare.flabook.dto.mapper.GambleMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.Gamble;
import com.msquare.flabook.models.ImageResource;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.BaseView.class)
public class GambleDto implements Serializable {

    private Long id;

    @JsonIgnoreProperties("gamble")
    private List<GambleItemDto> items;

    private String title;

    @JsonView(Views.WebAdminJsonView.class)
    private boolean active;

    //@JsonView(Views.WebAdminJsonView.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int maxAttempt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer attempt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean already;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int retryHour;

    @JsonView(Views.WebAdminJsonView.class)
    private ZonedDateTime initDate;

    @JsonView(Views.WebAdminJsonView.class)
    private ZonedDateTime startDate;

    @JsonView(Views.WebAdminJsonView.class)
    private ZonedDateTime endDate;

    @JsonView(Views.WebAdminJsonView.class)
    private String url;

    @JsonView(Views.WebAdminJsonView.class)
    private ImageResource resource;

    @JsonView(Views.WebAdminJsonView.class)
    private String photoUrl;

    public String getPhotoUrl() {
        if (photoUrl != null)
            return photoUrl;

        if(resource == null)
            return null;

        return ServiceHost.getS3Url(resource.getFilekey());
    }

    public void setPhotoUrl(String url) {
        this.photoUrl = url;
    }

    public static GambleDto of(Gamble gamble) {
        return GambleMapper.INSTANCE.of(gamble);
    }
}
