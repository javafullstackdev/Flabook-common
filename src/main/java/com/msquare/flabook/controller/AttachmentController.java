package com.msquare.flabook.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.service.AttachmentService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    /**
     * 태그 등록
     * @param id attachment Id
     * @param name 등록할 태그명
     * @return boolean
     */
    @PostMapping(value = "{id}/tag")
    public CommonResponse<Boolean> doAddTag(@PathVariable long id, @RequestParam String name) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), attachmentService.addTag(id, name));
    }

    /**
     * 태그 삭제
     * @param id attachment Id
     * @param name 삭제할 태그명
     * @return boolean
     */
    @DeleteMapping(value = "{id}/tag")
    public CommonResponse<Boolean> doRemoveTag(@PathVariable long id, @RequestParam String name) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), attachmentService.removeTag(id, name));
    }

}
