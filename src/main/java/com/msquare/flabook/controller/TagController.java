package com.msquare.flabook.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.TagDto;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.enumeration.RecommendKeywordType;
import com.msquare.flabook.form.CreateTagByCmsVo;
import com.msquare.flabook.form.CreateTagVo;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.service.TagService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/tags")
public class TagController {

    private final TagService tagService;

    @JsonView(Views.BaseView.class)
    @PostMapping(path = "")
    public CommonResponse<TagDto> doCreateTag(@Valid CreateTagVo vo) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), tagService.createTag(vo));
    }

    @JsonView(Views.BaseView.class)
    @PostMapping(path = "/simple/{id}")
    public CommonResponse<TagDto> doSimplify(@PathVariable Long id) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), tagService.simplify(id));
    }

    @JsonView(Views.BaseView.class)
    @PostMapping(path = "/{plantId}/sync")
    public CommonResponse<List<TagDto>> doCreateTagSync(@PathVariable String plantId,  @Valid CreateTagByCmsVo vo) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), tagService.createTagByPlantId(plantId, vo));
    }

    @JsonView(Views.BaseView.class)
    @PostMapping(path = "/{plantId}/remove/sync")
    public CommonResponse<List<TagDto>> doRemoveTagSync(@PathVariable String plantId) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), tagService.removeTagByPlantId(plantId));
    }

    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/{name}")
    public CommonResponse<TagDto> doFindTags(@PathVariable String name) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), tagService.findTag(name));
    }

    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/autocomplete")
    public CommonResponse<List<String>> doAutoComplete(String q, @RequestParam(value = "postingType", required = false) PostingType postingType, @RequestParam(required = false, defaultValue = "0") int offset, @RequestParam(required = false, defaultValue = "10") int count) {
        List<String> keywords = tagService.search(offset, count, q);
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), keywords);
    }

    @GetMapping(path = "/recommended")
    public CommonResponse<List<String>> doFindRecommendedTags(@RequestParam(value = "postingType", required = false) PostingType postingType, @RequestParam(value = "type", required = false) String type, @RequestParam(required = false, defaultValue = "0") int offset, @RequestParam(required = false, defaultValue = "10") int count) {
        RecommendKeywordType selectedType;
        if(postingType != null) {
            selectedType = RecommendKeywordType.findRecommendKeywordType(postingType);
        } else {
            try {
                selectedType = RecommendKeywordType.valueOf(type);
            } catch (Exception e){
                selectedType = null;
            }
        }
        List<String> keywords = tagService.getRecommendKeywords(selectedType, offset, count);
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), keywords);
    }

    @GetMapping(path = "/index")
    public String index() {
        tagService.index();
        return "index";
    }


}
