package com.msquare.flabook.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.ShareDto;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.service.ShareService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/shares")
public class ShareController {

    private final ShareService shareService;

    @JsonView(Views.PostingLikeOnlyJsonView.class)
    @GetMapping(path = "/{uuid}")
    public CommonResponse<ShareDto> findShare(@PathVariable UUID uuid) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), shareService.findShare(uuid));
    }

}
