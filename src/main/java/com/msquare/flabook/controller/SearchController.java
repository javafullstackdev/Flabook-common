package com.msquare.flabook.controller;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.PhotoWriterDto;
import com.msquare.flabook.dto.PostingDto;
import com.msquare.flabook.dto.UserDto;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.PhotoRecentWriter;
import com.msquare.flabook.models.User;
import com.msquare.flabook.service.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("java:S4684")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/search")
@JsonView(Views.BaseView.class)
public class SearchController {
    private final UserService userService;
    private final PostingService postingService;
    private final LikePostingService likePostingService;
    private final PhotoAlbumService photoAlbumService;
    @Qualifier("searchCircuitBreaker")
    private final CircuitBreaker cb;

    private void listWith(List<PostingDto> postingDtoList, PostingType postingType, User currentUser) {
        if(postingType.equals(PostingType.magazine) || postingType.equals(PostingType.boast)) {
            postingService.listWithScrap(postingDtoList, currentUser);
        } else if(postingType.equals(PostingType.question)) {
            postingService.listWithAnswer(postingDtoList);
        } else if(postingType.equals(PostingType.photo)){
            postingDtoList.forEach(postingDto -> {
                boolean isLike = likePostingService.isLikePost(postingDto.getId(), currentUser);
                postingDto.setIsLike(isLike);
            });
        }
    }

    static boolean isTimelineOrder(String orderby, String sortby) {
        return "id".equalsIgnoreCase(orderby) && !"asc".equalsIgnoreCase(sortby);
    }

    /**
     * orderby 가 popular 일 경우는 timeline 조회 지원안함
     */
    @JsonView(Views.PostingUserActivityJsonView.class)
    @GetMapping(path = "")
    public CommonResponse<List<PostingDto>> doSearch(@ApiIgnore User currentUser, @RequestParam("postingType") PostingType postingType, @RequestParam(required = false) String q, @RequestParam(required = false) Long sinceId, @RequestParam(required = false) Long maxId, @RequestParam(required = false, defaultValue = "0") Integer offset, @RequestParam(required = false, defaultValue = "10") int count, @RequestParam(required = false, defaultValue = "id") String orderby, @RequestParam(required = false, defaultValue = "") String sortby, @RequestParam(required = false, defaultValue = "false") boolean mine, @RequestParam(required = false) Integer mostComments, @RequestParam(required = false) Integer day) {
        return cb.run(() -> {
            List<PostingDto> postingDtoList;
            if (isTimelineOrder(orderby, sortby)) {
                if(mostComments == null && day == null && (q == null || q.isEmpty()) && !mine) {
                    postingDtoList = postingService.findTimeline(currentUser, postingType.getClazz(), sinceId, maxId, count);
                } else {
                    PostingService.SearchTimelineRequest request = new PostingService.SearchTimelineRequest(currentUser, postingType.getClazz(), sinceId, maxId, count, q, mine, Optional.ofNullable(mostComments), Optional.ofNullable(day));
                    postingDtoList = postingService.searchTimeline(request);
                }

            } else {
                PostingService.SearchPageRequest request = new PostingService.SearchPageRequest(currentUser, postingType, offset, count, q, orderby, sortby, mine, Optional.ofNullable(mostComments), Optional.ofNullable(day));
                postingDtoList = postingService.search(request);
            }

            listWith(postingDtoList, postingType, currentUser);

            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingDtoList);

        }, throwable -> {

            if(throwable instanceof CallNotPermittedException) {
                log.error("SearchController.search : {}", throwable.getMessage());
            } else {
                log.error("SearchController.search", throwable);
            }

            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), Collections.emptyList(), throwable.getMessage());
        });
    }

    /**
     * 검색결과 갯수
     */
    @JsonView(Views.PostingUserActivityJsonView.class)
    @GetMapping(path = "/count")
    public CommonResponse<Integer> doSearchResultCount(@ApiIgnore User currentUser, @RequestParam("postingType") PostingType postingType, @RequestParam(required = false) String q, @RequestParam(required = false) Long sinceId, @RequestParam(required = false) Long maxId, @RequestParam(required = false, defaultValue = "0") Integer offset, @RequestParam(required = false, defaultValue = "10") int count, @RequestParam(required = false, defaultValue = "id") String orderby, @RequestParam(required = false, defaultValue = "false") boolean mine, @RequestParam(required = false) Integer mostComments) {
        return cb.run(() -> {
            PostingService.SearchTimelineRequest request = new PostingService.SearchTimelineRequest(currentUser, postingType.getClazz(), sinceId, maxId, 10, q, mine, Optional.ofNullable(mostComments), Optional.empty());
            int result = postingService.searchResultCount(request);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), result);
        }
        , throwable -> new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), 0, throwable.getMessage()));
    }

    /**
     * orderby 가 popular 일 경우는 timeline 조회 지원안함
     *
     * @param q 검색어
     * @param count 갯수
     * @return CommonResponse<List<UserDto>>
     */
    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/users")
    public CommonResponse<List<UserDto>> doSearchUser(@RequestParam(required = false) String q, @RequestParam(required = false, defaultValue = "20") int count) {
        return cb.run(() -> {
            int cnt = (count > 20) ? 0 : count;
            List<UserDto> userDtoList = userService.searchTimeline(null, null, cnt, q);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), userDtoList);
        }, throwable -> {
            log.error("SearchController.doSearchUser", throwable);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), Collections.emptyList());
        });
    }


    /**
     * 포토 - 작가별
     * 기본 : id- 최근에 사진을 올린 최신순
     * orderby : photoCount-사진 많은순
     *          photoLikeCount-인기순
     * */
    private final PhotoRecentWriterService photoRecentWriterService;
    @SuppressWarnings("unused")
    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/writerUsers")
    public CommonResponse<List<PhotoWriterDto>> doSearchWriterUsers(@ApiIgnore User currentUser, @RequestParam(required = false, defaultValue = "id") String orderby, @RequestParam(required = false, defaultValue = "0") Integer offset, @RequestParam(required = false) Long sinceId, @RequestParam(required = false) Long maxId, @RequestParam(required = false, defaultValue = "10") int count, @RequestParam(required = false) Integer day) {
        return cb.run(() -> {

            List<PhotoWriterDto> photoWriterDtoList = new ArrayList<>();
            List<UserDto> userDtoList;
            if(orderby.equals("id") || orderby.equals("recent")){
                photoWriterDtoList = userService.findWriterTimeline(PhotoRecentWriter.class, sinceId, maxId, count);
                for(PhotoWriterDto dto : photoWriterDtoList) {
                    dto.setPhotos(photoAlbumService.findWritersRepresentPhotos(dto.getUser().getId()));
                }
            }
            else {
                userDtoList = userService.searchWriterTimeline(User.class, sinceId, maxId, offset, count,  orderby, day);
                for(UserDto userDto : userDtoList){
                    photoWriterDtoList.add(new PhotoWriterDto(userDto, photoAlbumService.findWritersRepresentPhotos(userDto.getId())));
                }
            }

            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), photoWriterDtoList);
        }, throwable -> {
            log.error("SearchController.doSearchUser", throwable);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), Collections.emptyList());
        });
    }

}
