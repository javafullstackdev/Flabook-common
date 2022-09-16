package com.msquare.flabook.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.api.service.HomeService;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.*;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.Comment;
import com.msquare.flabook.models.Posting;
import com.msquare.flabook.models.Ranking;
import com.msquare.flabook.models.User;
import com.msquare.flabook.service.PostingService;
import com.msquare.flabook.service.RankingService;
import com.msquare.flabook.util.RequestParamValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


class CommentLite {
    private String comment;

    public String getComment() {
        return this.comment;
    }

    private ArrayList<String> comment_photo_links;

    public ArrayList<String> getCommentPhotoLinks() {
        return this.comment_photo_links;
    }

    CommentLite(String comment, ArrayList<String> photo_links) {
        this.comment = comment;
        this.comment_photo_links = photo_links;
    }
}

class AIData {
    private Long id;
    private String title;
    private String text;
    private ArrayList<String> clinic_photo_link;
    private ArrayList<CommentLite> comments;

    AIData(long id, String title, String text, ArrayList<String> photo_links, ArrayList<CommentLite> comments) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.clinic_photo_link = photo_links;
        this.comments = comments;
    }

    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getText() {
        return this.text;
    }

    public ArrayList<String> getClinicPhotoLink() {
        return this.clinic_photo_link;
    }

    public List<CommentLite> getComments() {
        return this.comments;
    }
}

@SuppressWarnings("java:S4684")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/home")
public class HomeController {

    private static final List<PostingDto> emptyPostingList = Collections.emptyList();
    private static final List<GoodsDto> emptyGoodsList = Collections.emptyList();
    private static final List<BannerDto> emptyBannerList = Collections.emptyList();

    private final PostingService postingService;
    private final HomeService homeService;
    private final RankingService rankingService;
    private final EntityManager em;

    @Qualifier("homeCircuitBreaker")
    private final CircuitBreaker cb;

    @JsonView(Views.BaseView.class)
    @GetMapping(path = "")
    public CommonResponse<List<PostingDto>> doSearchHome(@ApiIgnore User currentUser, @RequestParam(value = "postingType", defaultValue = "question") PostingType postingType, @RequestParam(required = false) String q, @RequestParam(defaultValue = "0") int offset, @RequestParam(required = false, defaultValue = "5") int count, @RequestParam(defaultValue = "id") String orderBy, @RequestParam(required = false, defaultValue = "") String sortBy) {
        try {
            RequestParamValidator.validateOrderBy(orderBy);
        } catch (IllegalArgumentException e) {
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), Collections.emptyList(), e.getMessage());
        }

        return cb.run(() -> {
            if(postingType.equals(PostingType.photo)){
                List<PostingDto> postingDtoList = rankingService.findPostingRanking(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Asia/Seoul")), Ranking.RankingType.best_photo);
                return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingDtoList);
            }

            List<PostingDto> postingDtoList = postingService.findTimeline(currentUser, postingType.getClazz(), null, null, count);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingDtoList);
        }, throwable -> {
            log.error("PostingController.search", throwable);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), Collections.emptyList(), throwable.getMessage());
        });
    }

    /* Getting sample plant clinic data for AI modeling */
    @GetMapping(path = "/ai")
    public List<AIData> doAIPlantClinic(@ApiIgnore User currentUser, @RequestParam(defaultValue = "0") int offset, @RequestParam(required = false, defaultValue = "5") int count, @RequestParam(defaultValue = "id") String orderBy, @RequestParam(required = false, defaultValue = "") String sortBy) {
        try {
            RequestParamValidator.validateOrderBy(orderBy);
        } catch (IllegalArgumentException e) {
//            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), Collections.emptyList(), e.getMessage());
        }

            List<AIData> results = new ArrayList<>();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<?> query = cb.createQuery(PostingType.clinic.getClazz());

            List<Predicate> predicates = new ArrayList<>();
            Root<? extends Posting> root = query.from(PostingType.clinic.getClazz());

            query.where(predicates.toArray(new Predicate[]{})).orderBy(cb.desc(root.get("id")));

//            offset = 5001;
            int maxItems = 10;
            List<PostingDto> postings = em.createQuery(query).setFirstResult(offset).setMaxResults(maxItems).getResultList().stream().map(o -> PostingDto.of((Posting)o)).collect(Collectors.toList());

            for (PostingDto posting: postings) {

                String queryBuffer = "select * from comment a join ( select comment0_.id id from comment comment0_ where comment0_.posting_id=" +
                        posting.getId() +
                        " and comment0_.parent_id is null order by id desc limit " +
                        count +
                        " ) b on a.id = b.id order by a.id desc";

                List<CommentDto> comments_per_posting = ((List<Comment>)em.createNativeQuery(queryBuffer, Comment.class).getResultList()).stream().map(CommentDto::of).collect(Collectors.toList());

                ArrayList<String> clinic_photo_links = new ArrayList<>();
                for (AttachmentDto att: posting.getAttachments()) {
                    String clinic_photo_link = att.getPhotoUrl();
                    if (clinic_photo_link != null) clinic_photo_links.add(att.getPhotoUrl());
                }

                ArrayList<CommentLite> comments = new ArrayList<>();

                for (int i = 0; i < comments_per_posting.size(); i ++) {
                    CommentDto comment = comments_per_posting.get(i);
                    ArrayList<String> photo_links_per_comment = new ArrayList<>();
                    for (AttachmentDto att: comment.getAttachments()) {
                        if (att.getPhotoUrl() != null) photo_links_per_comment.add(att.getPhotoUrl());
                    }
                    comments.add(new CommentLite(comment.getText(), photo_links_per_comment));
                    if (!comment.getChildren().isEmpty()) comments_per_posting.addAll(comment.getChildren());
                }

                results.add(new AIData(posting.getId(), posting.getTitle(), posting.getText(), clinic_photo_links, comments));
            }

            return new ArrayList<>(results);
    }

    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/all")
    public CommonResponse<HomeTotalDto> doSearchHomeAll(@ApiIgnore User currentUser) {
        return cb.run(() -> {
            HomeTotalDto homeTotalDto = homeService.findAll(currentUser);
//            HomeTotalDto homeTotalDto = homeService.all(currentUser);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), homeTotalDto);
        }, throwable -> {
            log.error("HomeController.doSearchHomeAll", throwable);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), new HomeTotalDto(emptyPostingList, emptyPostingList, emptyPostingList, emptyPostingList, emptyPostingList, emptyPostingList, emptyPostingList, emptyGoodsList, emptyBannerList, emptyPostingList), throwable.getMessage());
        });
    }

    /**
     * HOME CRUD 확인
     * */
    @PostMapping("/createhome")
    public void createHome(@RequestParam("genre") String genre){
        homeService.createHomeGenre(genre);
    }

    @PutMapping("/updatehomeorder")
    public void updateHomeOrder(@RequestParam("genres") List<String> genres){
        homeService.updateHomeOrder(genres);
    }

    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/order")
    public CommonResponse<List<String>> doFindOrderChange() {
        return cb.run(() -> new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), homeService.getHomeOrder())
            , throwable -> {
            log.error("HomeController.doFindOrder", throwable);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), Collections.emptyList(), throwable.getMessage());
        });
    }
}
