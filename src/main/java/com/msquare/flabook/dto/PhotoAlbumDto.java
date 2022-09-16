package com.msquare.flabook.dto;


import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.mapper.PhotoAlbumMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.PhotoAlbum;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@JsonView({Views.BaseView.class})
public class PhotoAlbumDto implements Serializable {

    private long id;
    private String name;
    private Boolean isDelete;
//    private long ownerId;

    private PostingDto representPhoto;
    private ZonedDateTime createdAt;
    private int photoCnt;

    public static PhotoAlbumDto of(PhotoAlbum photoAlbum) {
        return PhotoAlbumMapper.INSTANCE.of(photoAlbum);
    }

}
