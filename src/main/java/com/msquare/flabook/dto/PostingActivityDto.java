package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.mapper.PostingActivityMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.Posting;

import java.io.Serializable;

@JsonView(Views.BaseView.class)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class PostingActivityDto implements Serializable {

    private static final long serialVersionUID = 1L;

    //@JsonView({Views.PostingLikeOnlyJsonView.class, Views.PostingUserActivityJsonView.class})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonView(Views.PostingLikeOnlyJsonView.class)
    @JsonProperty("isLike")
    private Boolean isLike;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("isScrap")
    @JsonView(Views.PostingScrapOnlyJsonView.class)
    private Boolean isScrap;

    /**
     * 상태별 앱 버튼 텍스트
     * null : 알림 받기
     * true : 알림 그만받기
     * false : 알림 다시받기
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonView({Views.PostingWatchOnlyJsonView.class})
    @JsonProperty("isWatch")
    private Boolean isWatch;

    public static PostingActivityDto of(Posting posting) {
        return  PostingActivityMapper.INSTANCE.of(posting);
    }
}
