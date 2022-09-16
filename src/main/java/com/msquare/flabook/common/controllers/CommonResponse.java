package com.msquare.flabook.common.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.experimental.Accessors;
import com.msquare.flabook.dto.CommentDto;
import com.msquare.flabook.dto.PostingDto;
import com.msquare.flabook.json.Views;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Data
@JsonView(Views.BaseView.class)
public class CommonResponse<T> implements Serializable {

    public CommonResponse(int resultCode, T resultData, String resultMsg) {
        super();
        this.resultCode = resultCode;
        this.resultMessage = resultData;
        this.resultMsg = resultMsg;
        setResultParams(resultCode, resultData, resultMsg);
    }

    public CommonResponse(int resultCode, T resultData) {
        super();
        this.resultCode = resultCode;
        this.resultMessage = resultData;
        setResultParams(resultCode, resultData, resultMsg);
    }

    public CommonResponse(CommonResponseCode result, T resultData) {
        super();
        this.resultCode = result.getResultCode();
        this.resultMsg = result.getResultMessage();
        this.resultMessage = resultData;
        setResultParams(resultCode, resultData, resultMsg);
    }

    // resultCode
    // 1000 : success,,,,,
    @JsonProperty("resultCode")
    private int resultCode;

    @JsonProperty("resultData")
    private T resultMessage;

    // return detail message
    @JsonProperty("resultMsg")
    private String resultMsg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Accessors(chain = true)
    private Paging<?> paging;

    @SuppressWarnings("unused")
    public CommonResponse<T> setPagingPostingTimeline(List<PostingDto> list, Integer count) {
        long max = (!list.isEmpty()) ? list.get(0).getId() : -1L;
        long min = (!list.isEmpty()) ? list.get(list.size() - 1).getId() - 1L : -1L;
        if(1L > min) min = -1L;

        this.paging = new Paging<>(max, min, count);
        return this;
    }

    @SuppressWarnings("unused")
    public CommonResponse<T> setPagingCommentTimeline(List<CommentDto> list, Integer count) {
        long max = (!list.isEmpty()) ? list.get(0).getId() : -1L;
        long min = (!list.isEmpty()) ? list.get(list.size() - 1).getId() - 1L : -1L;
        if(1L > min) min = -1L;

        this.paging = new Paging<>(max, min, count);
        return this;
    }

    @SuppressWarnings("unused")
    protected void setResultParams(int resultCode, T resultData, String resultMsg) {
    	if(RequestContextHolder.getRequestAttributes() != null) {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(requestAttributes == null)
                return;

            HttpServletRequest req = requestAttributes.getRequest();
            req.setAttribute("resultCode", resultCode);
            req.setAttribute("resultMsg", resultMsg);
        }
    }
}
