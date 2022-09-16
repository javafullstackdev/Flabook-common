package com.msquare.flabook.controller;

import com.drew.imaging.ImageProcessingException;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonMessages;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.*;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.form.CreateCommentVo;
import com.msquare.flabook.form.CreatePostVo;
import com.msquare.flabook.form.CreateReportVo;
import com.msquare.flabook.form.UpdatePostVo;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.User;
import com.msquare.flabook.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@SuppressWarnings({"java:S4684"})
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/postings")
public class PostingController {

    private final LikePostingService likePostingService;
    private final PostingService postingService;
    private final WatchPostingService watchPostingService;
    private final CommentService commentService;
    private final ScrapService scrapService;
    private final ShareService shareService;
    private final ReportPostingService reportPostingService;
    @Qualifier("postingCircuitBreaker")
    private final CircuitBreaker cb;

    @JsonView(Views.BaseView.class)
    @PostMapping(path = "")
    public CommonResponse<PostingDto> doCreatePosting(@Valid CreatePostVo createPostVo, @ApiIgnore User currentUser) throws ExecutionException, InterruptedException {
        PostingDto postingDto = postingService.createPosting(createPostVo, currentUser);
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingDto, "test");
    }


    @JsonView(Views.BaseView.class)
    @PutMapping( path = "/{id}")
    public CommonResponse<PostingDto> doUpdatePosting(@PathVariable("id") Long id, @Valid UpdatePostVo updatePostVo, @ApiIgnore User currentUser) throws ImageProcessingException, IOException, InterruptedException {
        PostingDto postingDto = postingService.updatePosting(id, updatePostVo, currentUser);
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingDto, "test");
    }

    @SuppressWarnings({"unused", "java:S4684"})
    @PostMapping(path = "/index")
    public boolean doIndex(@ApiIgnore User currentUser, @RequestParam String ids, @RequestParam(required = false) Integer max) {
        return postingService.indexing(Arrays.stream(StringUtils.split(ids, ",")).map(Long::valueOf).collect(Collectors.toList()), max);
    }

    @SuppressWarnings({"unused", "java:S4684"})
    @JsonView(Views.PostingActivityDetailJsonView.class)
    @GetMapping(path = "/{id}")
    public CommonResponse<PostingDto> doReadPosting(@PathVariable long id, @ApiIgnore User currentUser) {
        return cb.run(() -> postingService.findPosting(id)
                .map(postingDto -> {
                    boolean isLike = likePostingService.isLikePost(id, currentUser);
                    postingDto.setIsLike(isLike);
                    return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingDto);
                })
                .orElseGet(() -> new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, "게시글을 찾을 수 없습니다.")), throwable -> {
            log.error("readPosting", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, "잠시 후 다시 접속해주세요.");
        });
    }

    @JsonView(Views.PostingUserActivityJsonView.class)
    @GetMapping(path = "/{id}/activity")
    public CommonResponse<PostingActivityDto> doFindPostingActivity(@PathVariable long id, @ApiIgnore User currentUser) {
        return cb.run(() -> new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingService.findPostingActivity(id, currentUser)), throwable -> {
            log.error("readPosting", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, "잠시 후 다시 접속해주세요.");
        });
    }

    @JsonView(Views.BaseView.class)
    @PostMapping(path = "/{id}/comments")
    public CommonResponse<CommentDto> doCreateComment(@PathVariable long id, CreateCommentVo vo, @ApiIgnore User currentUser) throws ImageProcessingException, IOException, InterruptedException {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingService.createComment(id, vo, currentUser));
    }

    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/{id}/comments")
    public CommonResponse<List<CommentDto>> doFindCommentList(@PathVariable long id, @RequestParam(required = false) Long sinceId, @RequestParam(required = false) Long maxId, @RequestParam(required = false, defaultValue = "5") int count) {
        return cb.run(() -> {
            List<CommentDto> commentDtoList = commentService.findTimeline(id, sinceId, maxId, count);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), commentDtoList).setPagingCommentTimeline(commentDtoList, count);
        }, throwable -> new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), Collections.emptyList(), throwable.getMessage()));
    }

    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/{id}/comments/first")
    public CommonResponse<CommentDto> doFindFirstComment(@PathVariable long id) {
        return cb.run(() -> new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), commentService.findFirst(id).orElse(null)), throwable -> new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, throwable.getMessage()));
    }

    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/{id}/comments/adopt")
    public CommonResponse<CommentDto> doFindAdopedComment(@PathVariable long id) {
        return cb.run(() -> new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), commentService.findAdopted(id).orElse(null)), throwable -> new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, throwable.getMessage()));
    }

    //update
    @JsonView(Views.BaseView.class)
    @PostMapping(path = "/{id}/like")
    public CommonResponse<Boolean> doCreateLike(@PathVariable long id, @ApiIgnore User currentUser) {
        return cb.run(() -> {
            try {
                likePostingService.addLike(id, currentUser);
                return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), true);
            } catch (EntityNotFoundException e) {
                return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, CommonMessages.NOT_FOUND_POSTING);
            }
        }, throwable -> {
            log.error("doCreateLike", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, throwable.getMessage());
        });
    }

    @JsonView(Views.BaseView.class)
    @DeleteMapping(path = "/{id}/like")
    public CommonResponse<Boolean> doRemoveLike(@PathVariable long id, @ApiIgnore User currentUser) {
        return cb.run(() -> {
            try {
                likePostingService.removeLike(id, currentUser);
                return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), false);
            } catch (EntityNotFoundException e) {
                return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, CommonMessages.NOT_FOUND_POSTING);
            }
        }, throwable -> {
            log.error("doRemoveLike", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, throwable.getMessage());
        });
    }

    @JsonView(Views.BaseView.class)
    @PostMapping(path = "/{id}/watch")
    public CommonResponse<Boolean> doCreateWatch(@PathVariable long id, @ApiIgnore User currentUser) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), watchPostingService.watch(id, currentUser));
    }

    @JsonView(Views.BaseView.class)
    @DeleteMapping(path = "/{id}/watch")
    public CommonResponse<Boolean> doRemoveWatch(@PathVariable long id, @ApiIgnore User currentUser) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), watchPostingService.unwatch(id, currentUser));
    }

    @PostMapping(path = "/{id}/scrap")
    public CommonResponse<Boolean> doCreateScrap(@PathVariable Long id, @ApiIgnore User currentUser) {

        return cb.run(() -> {
            try {
                return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), scrapService.addScrap(id, currentUser.getId()));
            } catch (EntityNotFoundException e) {
                return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, CommonMessages.NOT_FOUND_POSTING);
            }
        }, throwable -> {
            log.error("doCreateScrap", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, throwable.getMessage());
        });
    }

    @DeleteMapping(path = "/{id}/scrap")
    public CommonResponse<Boolean> doRemoveScrap(@PathVariable Long id, @ApiIgnore User currentUser) {

        return cb.run(() -> {
            try {
                return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), scrapService.removeScrap(id, currentUser.getId()));
            } catch (EntityNotFoundException e) {
                return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, CommonMessages.NOT_FOUND_POSTING);
            }
        }, throwable -> {
            log.error("doRemoveScrap", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, throwable.getMessage());
        });
    }

    @PostMapping(path = "/{id}/report")
    public CommonResponse<Boolean> doCreateReport(@ApiIgnore User currentUser, @PathVariable Long id, @Valid CreateReportVo vo) {
        return cb.run(() -> {
            try {
                return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), reportPostingService.addReport(id, currentUser.getId(), vo));
            } catch (EntityNotFoundException e) {
                return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, CommonMessages.NOT_FOUND_POSTING);
            }
        }, throwable -> {
            log.error("doCreateReport", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, throwable.getMessage());
        });

    }

    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/{id}/similarContent")
    public CommonResponse<List<PostingDto>> doFindSimilarContentList(@PathVariable Long id, @RequestParam(defaultValue = "score") String orderby, @RequestParam(defaultValue = "desc") String sortby, @RequestParam(defaultValue = "0") Integer offset, @RequestParam(defaultValue = "5") Integer count) {

        return cb.run(() -> new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingService.findSimilarContentList(id, offset, count, orderby, sortby)), throwable -> {
            log.error("findSimilarContentList", throwable);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), Collections.emptyList());
        });

    }

    @JsonView(Views.BaseView.class)
    @DeleteMapping(path = "/{id}")
    public CommonResponse<PostingDto> doRemovePosting(@PathVariable Long id, @ApiIgnore User currentUser) {
        return cb.run(() -> {
            try {
                return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingService.deletePosting(id, currentUser));
            } catch (FlabookGlobalException e) {
                return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, e.getMessage());
            }
        }, throwable -> {
            log.error("doRemovePosting", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, throwable.getMessage());
        });
    }

    @JsonView(Views.BaseView.class)
    @PostMapping(path = "/{id}/share")
    public CommonResponse<ShareDto> doShare(@ApiIgnore User owner, @PathVariable Long id) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), shareService.doSharePosting(owner, id));
    }


    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/{id}/users")
    public CommonResponse<List<UserIdWithNicknameDto>> doFindMentionUsers(@PathVariable Long id, @RequestParam(value = "q", defaultValue = "", required = false) String query) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingService.findPostingMentionUsers(id, query));
    }

    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/{id}/goodses")
    public CommonResponse<List<GoodsDto>> doFindMentionUsers(@PathVariable Long id) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingService.findRelationGoodses(id));
    }
}
