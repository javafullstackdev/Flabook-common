package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
public class MetatagDto implements Serializable {

    private static final long serialVersionUID = 4616571874498515685L;

    private String title;
    private String url;
    private String image;
    private String description;

    private String goodsNo;

    @JsonIgnore
    public ZonedDateTime expiresAt = ZonedDateTime.now().plusMinutes(10L);

    public void setImageUrl(String imageUrl) {
        this.image = imageUrl;
    }

    public void setGoodsNm(String name) {
        this.title = name;
    }

    public void setShortDescription(String description) {
        this.description = description;
    }


    /**
    json : {"goodsNm":"[꽃모종] 꽃꼬리풀(베로니카) 블루 10cm 포트","goodsNo":"1000007260","shortDescription":"","image":"1000007260_main_077.jpg","imageStorage":"local","imagePath":"20\/01\/04\/1000007260\/","imageUrl":"http:\/\/m.flabook.godomall.com\/data\/goods\/20\/01\/04\/1000007260\/1000007260_main_077.jpg","code":"0"}
    */
    @SuppressWarnings("unused")
    public MetatagDto(String title, String description, String image, String url) {
        super();
        this.title = title;
        this.url = url;
        this.image = image;
        this.description = description;
    }

    public MetatagDto(String title, String description, String image, String url, String goodsNo) {
        super();
        this.title = title;
        this.url = url;
        this.image = image;
        this.description = description;
        this.goodsNo = goodsNo;
    }


}
