package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.msquare.flabook.json.Views;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.BaseView.class)
public class PhotoWriterDto implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long timelineId;

    private UserDto user;

    private List<PostingDto> photos;

    public PhotoWriterDto(UserDto user, List<PostingDto> photos) {
        this.user = user;
        this.photos = photos;
    }
}
