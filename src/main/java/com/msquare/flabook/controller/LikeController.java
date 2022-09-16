package com.msquare.flabook.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.PostingDto;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.User;
import com.msquare.flabook.service.PostingService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@SuppressWarnings("java:S4684")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/likes")
public class LikeController {

    private final PostingService postingService;
    @Qualifier("likeCircuitBreaker")
    private final CircuitBreaker cb;

    @JsonView(Views.PostingLikeOnlyJsonView.class)
    @GetMapping(path = "")
    public CommonResponse<List<PostingDto>> doFindList(@ApiIgnore User currentUser, @RequestParam(value = "type", required = false) PostingType postingType, @RequestParam(required = false) String q, @RequestParam(required = false) Long sinceId, @RequestParam(required = false) Long maxId, @RequestParam(required = false, defaultValue = "10") int count) {
        return cb.run(() -> {
            List<PostingDto> postingDtoList = postingService.findLikeListByUser(currentUser.getId(), postingType, sinceId, maxId, count);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingDtoList);
        });
    }


}
