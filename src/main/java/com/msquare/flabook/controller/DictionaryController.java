package com.msquare.flabook.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.ShareDto;
import com.msquare.flabook.dto.TagDto;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.User;
import com.msquare.flabook.service.ShareService;
import com.msquare.flabook.service.TagService;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@SuppressWarnings("java:S4684")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/dictionaries")
public class DictionaryController {

    private final TagService tagService;
    private final ShareService shareService;

    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/{name}")
    public CommonResponse<String> doFindDictionary(@PathVariable String name) {
        TagDto tagDto = tagService.findTag(name);
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), "http://cms.flabook.co.kr/plant/" + tagDto.getName());
    }

    @JsonView(Views.BaseView.class)
    @PostMapping(path = "/{name}/share")
    public CommonResponse<ShareDto> doShareDictionary(@PathVariable String name, @ApiIgnore User currentUser) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), shareService.doShareDictionary(currentUser, name));
    }

}
