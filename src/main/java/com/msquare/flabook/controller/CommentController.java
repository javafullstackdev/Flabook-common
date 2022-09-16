package com.msquare.flabook.controller;

import com.drew.imaging.ImageProcessingException;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonMessages;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.CommentDto;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.form.CreateCommentVo;
import com.msquare.flabook.form.CreateReportVo;
import com.msquare.flabook.form.UpdateCommentVo;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.User;
import com.msquare.flabook.service.CommentService;
import com.msquare.flabook.service.PostingService;
import com.msquare.flabook.service.ReportCommentService;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.IOException;

@SuppressWarnings("java:S4684")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/comments")
public class CommentController {

    private final PostingService postingService;
    private final CommentService commentService;
    private final ReportCommentService reportCommentService;

    @JsonView(Views.BaseView.class)
    @PutMapping(path = "/{id}")
    public CommonResponse<CommentDto> doCreateComment(@PathVariable long id, @Valid UpdateCommentVo vo, @ApiIgnore User currentUser) throws ImageProcessingException, IOException, InterruptedException {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), commentService.updateComment(id, vo, currentUser));
    }

    @JsonView(Views.BaseView.class)
    @DeleteMapping(path = "/{id}")
    public CommonResponse<CommentDto> doDeleteComment(@PathVariable long id, @ApiIgnore User currentUser) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingService.deleteComment(id, currentUser));
    }

    @JsonView(Views.BaseView.class)
    @PostMapping(path = "/{id}/reply")
    public CommonResponse<CommentDto> doCreateReply(@PathVariable long id, @Valid CreateCommentVo vo, @ApiIgnore User currentUser) throws InterruptedException, IOException, FlabookGlobalException, ImageProcessingException {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingService.createReply(id, vo, currentUser, false, false));
    }

    @JsonView(Views.BaseView.class)
    @PostMapping(path = "/{id}/like")
    public CommonResponse<Boolean> doLike(@PathVariable long id, @ApiIgnore User currentUser) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), commentService.toggleLike(id, currentUser.getId()));
    }

    @JsonView(Views.BaseView.class)
    @PostMapping(path = "/{id}/adopt")
    public CommonResponse<CommentDto> doAdopt(@PathVariable long id, @Valid CreateCommentVo vo, @ApiIgnore User currentUser) throws InterruptedException, ImageProcessingException, IOException {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingService.createReply(id, vo, currentUser, false, true));
    }

    @JsonView(Views.BaseView.class)
    @PostMapping(path = "/{id}/thanks")
    public CommonResponse<CommentDto> doThanks(@PathVariable long id, @Valid CreateCommentVo vo, @ApiIgnore User currentUser) throws InterruptedException, ImageProcessingException, IOException {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingService.createReply(id, vo, currentUser, true, false));
    }

    @PostMapping(path = "/{id}/report")
    public CommonResponse<Boolean> doCreateComment(@ApiIgnore User currentUser, @PathVariable Long id, @Valid CreateReportVo vo) {
        try {
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), reportCommentService.addReport(id, currentUser.getId(), vo));
        } catch (EntityNotFoundException e) {
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, CommonMessages.NOT_FOUND_POSTING);
        }
    }


}
