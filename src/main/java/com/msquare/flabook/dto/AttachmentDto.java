package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.mapper.AttachmentMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.Attachment;
import com.msquare.flabook.models.ImageResource;
import com.msquare.flabook.common.configurations.ServiceHost;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.BaseView.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttachmentDto implements Serializable {

    private long id;

    private String description;

    @JsonIgnore
    private ImageResource imageResource;

    private String photoUrl = null;

    private String dimension;

    private String placeName;

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

    public static AttachmentDto of(Attachment attachment) {
        return AttachmentMapper.INSTANCE.of(attachment);
    }

}
