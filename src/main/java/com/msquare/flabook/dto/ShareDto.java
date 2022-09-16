package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.mapper.ShareMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.Resource;
import com.msquare.flabook.models.Share;

import java.io.Serializable;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.BaseView.class)
public class ShareDto implements Serializable {

    //https://blog.deliwind.com/posts/235

    private UUID uuid;

    @JsonView(Views.BaseView.class)
    private Resource resource;

    private String deeplink;

    public static ShareDto of(Share share) {
        return ShareMapper.INSTANCE.of(share);
    }


}
