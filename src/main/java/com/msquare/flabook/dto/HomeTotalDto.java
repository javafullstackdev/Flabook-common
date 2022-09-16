package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.msquare.flabook.json.Views;

import java.io.Serializable;
import java.util.List;

@Data
@JsonView(Views.BaseView.class)
@NoArgsConstructor
@AllArgsConstructor
public class HomeTotalDto implements Serializable {
    private List<PostingDto> question;
    private List<PostingDto> magazine;
    private List<PostingDto> clinic;
    private List<PostingDto> guidebook;
    private List<PostingDto> free;
    private List<PostingDto> boast;
    private List<PostingDto> television;
    private List<GoodsDto> goods;
    private List<BannerDto> banner;
    private List<PostingDto> photo;
}
