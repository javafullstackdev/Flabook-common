package com.msquare.flabook.openapi.youtube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.msquare.flabook.json.Views;


/**

https://www.youtube.com/oembed?v=2&url=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DEbwzEEmZwvw&format=json&dnt=1

json : {
    author_name: "Netflix Korea"
    ,author_url: "https://www.youtube.com/c/NetflixKorea"
    ,height: 113
    ,html: "<iframe width=\"200\" height=\"113\" src=\"https://www.youtube.com/embed/EbwzEEmZwvw?feature=oembed\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"
    ,provider_name: "YouTube"
    ,provider_url: "https://www.youtube.com/"
    ,thumbnail_height: 360
    ,thumbnail_url: "https://i.ytimg.com/vi/EbwzEEmZwvw/hqdefault.jpg"
    ,thumbnail_width: 480
    ,title: "스토어웨이 | 공식 예고편 | Netflix"
    ,type: "video"
    ,version: "1.0"
    ,width: 200
}
 */
@JsonView(Views.BaseView.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbedMetaData {

    private String title;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("author_url")
    private String authorUrl;

    private int height;
    private int width;

    private String version;
    private String type;

    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;

    @JsonProperty("thumbnail_width")
    private int thumbnailWidth;

    @JsonProperty("thumbnail_height")
    private int thumbnailHeight;

    @JsonProperty("provider_name")
    private String providerName;
    @JsonProperty("provider_url")
    private String providerUrl;

    private String html;
}
