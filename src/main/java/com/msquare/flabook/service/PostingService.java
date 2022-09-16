package com.msquare.flabook.service;

import com.drew.imaging.ImageProcessingException;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import javassist.NotFoundException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonMessages;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.*;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.enumeration.UserRole;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.exception.FlabookPermissionException;
import com.msquare.flabook.form.*;
import com.msquare.flabook.models.*;
import com.msquare.flabook.models.board.*;
import com.msquare.flabook.openapi.kakao.Address;
import com.msquare.flabook.repository.*;
import com.msquare.flabook.service.elasticsearch.CacheValues;
import com.msquare.flabook.util.AccessControl;
import com.msquare.flabook.util.GeoUtils;
import com.msquare.flabook.util.MentionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.sort.SortContext;
import org.hibernate.search.query.dsl.sort.SortFieldContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.msquare.flabook.util.ValidateNotPhotoUtils.validateNotPhoto;

@Slf4j
@Service
@AllArgsConstructor
public class PostingService extends AbstractSearchIndexer {

    /*public PostingService(@Lazy CommentService commentService, PostingRepository postingRepository, PostingRepositoryCustom postingRepositoryCustom, UserRepository userRepository, EntityManager em, CommentRepository commentRepository, ImageUploadService imageUploadService, NotificationService notificationService, LikePostingService likePostingService, ScrapService scrapService, TagRepository tagRepository, GoodsRepository goodsRepository, WatchPostingService watchPostingService, UserService userService, KakaoOpenApiService kakaoOpenApiService, AdoptCommentRepository adoptCommentActivityRepository, ThanksCommentRepository thanksCommentActivityRepository, PhotoAlbumService photoAlbumService, PhotoPhotoAlbumService photoPhotoAlbumService, PhotoRecentWriterRepository photoRecentWriterRepository, PhotoRecentWriterService photoRecentWriterService, ReportPostingService reportPostingService, RankingService rankingService) {
        this.commentService = commentService;
        this.postingRepository = postingRepository;
        this.postingRepositoryCustom = postingRepositoryCustom;
        this.userRepository = userRepository;
        this.em = em;
        this.commentRepository = commentRepository;
        this.imageUploadService = imageUploadService;
        this.notificationService = notificationService;
        this.likePostingService = likePostingService;
        this.scrapService = scrapService;
        this.tagRepository = tagRepository;
        this.goodsRepository = goodsRepository;
        this.watchPostingService = watchPostingService;
        this.userService = userService;
        this.kakaoOpenApiService = kakaoOpenApiService;
        this.adoptCommentActivityRepository = adoptCommentActivityRepository;
        this.thanksCommentActivityRepository = thanksCommentActivityRepository;
        this.photoAlbumService = photoAlbumService;
        this.photoPhotoAlbumService = photoPhotoAlbumService;
        this.photoRecentWriterRepository = photoRecentWriterRepository;
        this.photoRecentWriterService = photoRecentWriterService;
        this.reportPostingService = reportPostingService;
        this.rankingService = rankingService;
    }*/

    private final CommentService commentService;
    private final PostingRepository postingRepository;
    private final PostingRepositoryCustom postingRepositoryCustom;
    private final UserRepository userRepository;
    private final EntityManager em;
    private final CommentRepository commentRepository;
    private final ImageUploadService imageUploadService;
    private final NotificationService notificationService;
    private final LikePostingService likePostingService;
    private final ScrapService scrapService;
    private final TagRepository tagRepository;
    private final GoodsRepository goodsRepository;
    private final WatchPostingService watchPostingService;
    private final UserService userService;
    private final KakaoOpenApiService kakaoOpenApiService;
    private final AdoptCommentRepository adoptCommentActivityRepository;
    private final ThanksCommentRepository thanksCommentActivityRepository;
    private final PhotoAlbumService photoAlbumService;
    private final PhotoPhotoAlbumService photoPhotoAlbumService;
    private final PhotoRecentWriterRepository photoRecentWriterRepository;
    private final PhotoRecentWriterService photoRecentWriterService;
    private final ReportPostingService reportPostingService;
    private final RankingService rankingService;


    private static final Pattern pattern = Pattern.compile("#([0-9a-zA-Z가-힣]*)");

    protected static final Map<PostingType, List<String>> matchFields;
    static  {
        Map<PostingType, List<String>> temp = new EnumMap<>(PostingType.class);

        temp.put(PostingType.question, Collections.singletonList(Fields.COMMENTS_COMMENT_NOT_ANALYZED_TEXT));
        temp.put(PostingType.boast, Collections.unmodifiableList(Arrays.asList(Fields.POSTING_KOREAN_TEXT, Fields.ATTACHMENTS_ATTACHMENT_KOREAN_DESCRIPTION)));
        temp.put(PostingType.magazine, Collections.unmodifiableList(Arrays.asList(Fields.TITLE, Fields.COMMENTS_COMMENT_TEXT, Fields.ATTACHMENTS_ATTACHMENT_KOREAN_DESCRIPTION)));
        temp.put(PostingType.clinic, Collections.unmodifiableList(Arrays.asList(Fields.TITLE, Fields.COMMENTS_COMMENT_NOT_ANALYZED_TEXT, Fields.POSTING_KOREAN_TEXT)));
        temp.put(PostingType.free, Collections.unmodifiableList(Arrays.asList(Fields.POSTING_KOREAN_TEXT, Fields.COMMENTS_COMMENT_TEXT, Fields.ATTACHMENTS_ATTACHMENT_KOREAN_DESCRIPTION)));
        temp.put(PostingType.guidebook, Collections.unmodifiableList(Arrays.asList(Fields.POSTING_KOREAN_TEXT, Fields.ATTACHMENTS_ATTACHMENT_KOREAN_DESCRIPTION)));
        temp.put(PostingType.television, Collections.unmodifiableList(Arrays.asList(Fields.TITLE, Fields.POSTING_TEXT)));
        temp.put(PostingType.photo, Collections.singletonList(Fields.POSTING_TEXT));

        matchFields = Collections.unmodifiableMap(temp);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Modify {
        private String id;
        private String description;
        private MultipartFile file;
        private int seq;
    }


    @SuppressWarnings("java:S107")
    @Getter
    @NoArgsConstructor
    public static class SearchPageRequest {
        private User user;
        private PostingType postingType;
        private int offset;
        private int count;
        private String query;
        private String orderBy;
        private String sortBy;
        private boolean mine;
        private Optional<Integer> optionalBelowCommentCount;
        private Optional<Integer> optionalDays;
        //검색할 필드를 지정한다
        private Optional<String> optionalQueryTargetFields;

        public SearchPageRequest(User user, PostingType postingType, int offset, int count, String query, String orderBy, String sortBy, boolean mine, Optional<Integer> optionalBelowCommentCount, Optional<Integer> optionalDays) {
            this.user = user;
            this.postingType = postingType;
            this.offset = offset;
            this.count = count;
            this.query = query;
            this.orderBy = orderBy;
            this.sortBy = sortBy;
            this.mine = mine;
            this.optionalBelowCommentCount = optionalBelowCommentCount;
            this.optionalDays = optionalDays;
            this.optionalQueryTargetFields = Optional.empty();
        }

        public SearchPageRequest(User user, PostingType postingType, int offset, int count, String query, String orderBy, String sortBy, boolean mine, Optional<Integer> optionalBelowCommentCount, Optional<Integer> optionalDays, Optional<String> optionalQueryTargetFields) {
            this.user = user;
            this.postingType = postingType;
            this.offset = offset;
            this.count = count;
            this.query = query;
            this.orderBy = orderBy;
            this.sortBy = sortBy;
            this.mine = mine;
            this.optionalBelowCommentCount = optionalBelowCommentCount;
            this.optionalDays = optionalDays;
            this.optionalQueryTargetFields = optionalQueryTargetFields;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchRequestByTargetUser {
        private long userId;
        private PostingType postingType;
        private Long sinceId;
        private Long maxId;
        private int count;
        private List<String> targets;
        private Map<String, String[]> nestedQueryTargets;
        private Optional<String> optionalQuery;
    }

    @SuppressWarnings("java:S107")
    @Getter
    @NoArgsConstructor
    public static class SearchTimelineRequest {
        private User user;
        private Class<? extends Posting> clazz;
        private Long sinceId;
        private Long maxId;
        private int count;
        private String query;
        private boolean mine;
        private Optional<Integer> optionalBelowCommentCount;
        private Optional<Integer> optionalDays;
        private Optional<String> optionalQueryTargetFields;

        public SearchTimelineRequest(User user, Class<? extends Posting> clazz, Long sinceId, Long maxId, int count, String query, boolean mine, Optional<Integer> optionalBelowCommentCount, Optional<Integer> optionalDays) {
            this.user = user;
            this.clazz = clazz;
            this.sinceId = sinceId;
            this.maxId = maxId;
            this.count = count;
            this.query = query;
            this.mine = mine;
            this.optionalBelowCommentCount = optionalBelowCommentCount;
            this.optionalDays = optionalDays;
            this.optionalQueryTargetFields = Optional.empty();
        }

        public SearchTimelineRequest(User user, Class<? extends Posting> clazz, Long sinceId, Long maxId, int count, String query, boolean mine, Optional<Integer> optionalBelowCommentCount, Optional<Integer> optionalDays, Optional<String> optionalQueryTargetFields) {
            this.user = user;
            this.clazz = clazz;
            this.sinceId = sinceId;
            this.maxId = maxId;
            this.count = count;
            this.query = query;
            this.mine = mine;
            this.optionalBelowCommentCount = optionalBelowCommentCount;
            this.optionalDays = optionalDays;
            this.optionalQueryTargetFields = optionalQueryTargetFields;
        }
    }


    static class Fields {
        private Fields() {}

        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String TITLE_NOT_ANALYZED_TEXT = "title_not_analyzed_text";
        public static final String POSTING_TEXT = "posting_text";
        public static final String IS_DELETE = "isDelete";
        public static final String OWNER_ID = "owner.id";
        public static final String COMMENTS_OWNER_ID = "comments.ownerId";
        public static final String COMMENTS_CHILDREN_OWNER_ID = "comments.children.ownerId";
        public static final String CREATED_AT = "createdAt";
        public static final String COMMENT_COUNT = "commentCount";
        public static final String LIKE_COUNT = "likeCount";
        public static final String READ_COUNT = "readCount";
        public static final String COMMENTS_COMMENT_TEXT = "comments.comment_text";
        public static final String COMMENTS_CHILDREN_COMMENT_TEXT = "comments.children.comment_text";
        public static final String COMMENTS_COMMENT_NOT_ANALYZED_TEXT = "comments.comment_not_analyzed_text";
        public static final String ATTACHMENTS_ATTACHMENT_KOREAN_DESCRIPTION = "attachments.attachment_korean_description";
        public static final String POSTING_KOREAN_TEXT = "posting_korean_text";
    }

    @Override
    public Class<? extends Posting> getClazz() {
        return Posting.class;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheValues.POSTINGS, key = "#id")
    public Optional<PostingDto> findPosting(long id) {
        return postingRepository.findById(id).map(PostingDto::of);
    }

    @SuppressWarnings("unused")
    public List<Posting> findPostings(List<Long> ids) {
        return postingRepository.findAllById(ids);
    }

    //갯수 이상일 경우 query 만 업데이트 한다. 색인안함
    private static final int QUERY_UPDATE_MAX_COMMENTS = 20;
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    //@CacheEvict(value = CacheValues.POSTINGS, key = "#id")
    public void updateRead(long id, long count) {
        postingRepository.findLockOnly(id).ifPresent(posting -> {
            //** 주의 ** 댓글량이 많은 게시글은 여기서 검색 색인을 다시하지 않는다.
            if (posting.getCommentCount() > QUERY_UPDATE_MAX_COMMENTS) {
                postingRepository.updateReadCount(id, (int) count);
            } else {
                int sum = posting.getReadCount() + (int) count;
                posting.setReadCount(sum);
            }
        });
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @CacheEvict(value = CacheValues.POSTINGS, key = "#id")
    public void updateReceiving(long id, long count) {
        postingRepository.findLockOnly(id).ifPresent(posting -> {
            //** 주의 ** 댓글량이 많은 게시글은 여기서 검색 색인을 다시하지 않는다.
            if (posting.getCommentCount() > QUERY_UPDATE_MAX_COMMENTS) {
                postingRepository.updateReceivingCount(id, (int) count);
            } else {
                int sum = posting.getReceivingCount() + (int) count;
                posting.setReceivingCount(sum);
            }
        });
    }

    /**
     * 내가 좋아요한 글 목록
     */
    @Transactional(readOnly = true)
    public List<PostingDto> findLikeListByUser(long userId, PostingType postingType, Long sinceId, Long maxId, int count) {
        return postingRepositoryCustom.likeList(postingType, userId, sinceId, maxId, count).stream().map(PostingDto::of).collect(Collectors.toList());
    }

    /**
     * 내가 채택된 글 목록
     */
    @Transactional(readOnly = true)
    public List<PostingDto> findAdopedListByUser(long userId, PostingType postingType, Long sinceId, Long maxId, int count) {
        return postingRepositoryCustom.adoptedList(postingType, userId, sinceId, maxId, count).stream().map(PostingDto::of).collect(Collectors.toList());
    }

    /**
     * 내가 스크랩한 글 목록
     */
    @Transactional(readOnly = true)
    public List<PostingDto> findScrapListByUser(long userId, PostingType postingType, Long sinceId, Long maxId, int count) {
        return postingRepositoryCustom.crippingList(postingType, userId, sinceId, maxId, count).stream().map(PostingDto::of).collect(Collectors.toList());
    }


    //ownerId + isDelete
    private static final String COMMENT_OWNER_SELECTOR = "ownerId";

    private void predicateSinceId(BooleanJunction<?> booleanJunction, Long sinceId) {
        if (sinceId != null && sinceId > 0) {
            booleanJunction.must(NumericRangeQuery.newLongRange(Fields.ID, sinceId, Long.MAX_VALUE, false, true));
        }
    }

    private void predicateMaxId(BooleanJunction<?> booleanJunction, Long maxId) {
        if (maxId != null && maxId > 0) {
            booleanJunction.must(NumericRangeQuery.newLongRange(Fields.ID, Long.MIN_VALUE, maxId, true, true));
        }
    }

    private void predicateQueryByTargets(QueryBuilder qb, BooleanJunction<?> booleanJunction, List<String> targets, Map<String, String[]> nestedQueryTargets, Optional<String> optionalQuery, Long userId) {
        if(CollectionUtils.isEmpty(targets)) {
            return;
        }

        BooleanJunction<?> targetJunction = qb.bool();
        for(String target : targets) {
            //nested 검색이 되지 않으므로 (id 가 같고 isDelete 가 false 인) 별로 컬럼에 정의
            Object commentOwnerSelect = (target.contains(COMMENT_OWNER_SELECTOR)) ? String.format("%s%b", userId, false) : userId;

            if(target.indexOf("!") == 0) {
                booleanJunction.must(qb.keyword().onField(target.substring(1)).matching(commentOwnerSelect).createQuery()).not();
                continue;
            }

            String[] fields= nestedQueryTargets.get(target);
            boolean isEmpty = true;
            if(optionalQuery.isPresent() && !ArrayUtils.isEmpty(fields )) {
                for (String field : fields) {
                    if(StringUtils.isBlank(field)) {
                       continue;
                    }

                    //검색쿼리
                    String query = optionalQuery.get();
                    targetJunction.should(
                        qb.bool()
                            .must(qb.keyword().onField(target).matching(commentOwnerSelect).createQuery())
                            .must(qb.keyword().onField(field).matching(query).createQuery()).createQuery()
                    );
                    isEmpty = false;
                }
            }
            if(isEmpty) {
                targetJunction.should(qb.keyword().onField(target).matching(commentOwnerSelect).createQuery());
            }
        }

        if(!targetJunction.isEmpty()) {
            booleanJunction.must(targetJunction.createQuery());
        }
    }

    /**
     *
     * @return List<PostingDto>
     */
    @SuppressWarnings("unchecked")
    private List<PostingDto> searchUserContents(SearchRequestByTargetUser request) {

        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
        Class<? extends Posting> entityClass = (request.getPostingType() != null) ? PostingType.orElse(request.getPostingType(), PostingType.question).getClazz() : Posting.class;
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(entityClass)
                .get();

        BooleanJunction<?> booleanJunction = qb.bool();
        predicateSinceId(booleanJunction, request.getSinceId());
        predicateMaxId(booleanJunction, request.getMaxId());
        predicateQueryByTargets(qb, booleanJunction, request.getTargets(), request.getNestedQueryTargets(), request.getOptionalQuery(), request.getUserId());

        booleanJunction.must(qb.keyword().onField(Fields.IS_DELETE).matching(true).createQuery()).not();

        Query luceneQuery = booleanJunction.createQuery();
        Sort sort = qb.sort().byField(Fields.ID).desc().createSort();
        javax.persistence.Query persistenceQuery =
                fullTextEntityManager.createFullTextQuery(luceneQuery, entityClass)
                        .setFirstResult(0)
                        .setMaxResults(request.getCount()).setSort(sort);

        List<Posting> postings = persistenceQuery.getResultList();
        return postings.stream().map(PostingDto::of).collect(Collectors.toList());
    }

    /**
     * 내가 작성한 글 목록
     */
    @Transactional(readOnly = true)
    public List<PostingDto> searchUserPostings(long userId,  PostingType postingType, Long sinceId, Long maxId, int count, Optional<String> optionalQuery) {
        Map<String, String[]> nestedQueryTargets = new HashMap<>();
        nestedQueryTargets.put(Fields.OWNER_ID, new String[]{Fields.POSTING_TEXT, Fields.TITLE});
        SearchRequestByTargetUser request = new SearchRequestByTargetUser(userId, postingType, sinceId, maxId, count, Collections.singletonList(Fields.OWNER_ID), nestedQueryTargets, optionalQuery);
        return searchUserContents(request);
    }

    /**
     * 내가 쓴 글이 아니며 댓글은 단 글 목록
     */
    @Transactional(readOnly = true)
    public List<PostingDto> searchUserComments(long userId,  PostingType postingType, Long sinceId, Long maxId, int count, Optional<String> optionalQuery) {

        // 검색 조건일 경우 내가쓴 글의 댓글도 검색
        List<String> targets = (optionalQuery.isPresent()) ? Arrays.asList(Fields.COMMENTS_OWNER_ID, Fields.COMMENTS_CHILDREN_OWNER_ID) : Arrays.asList("!owner.id", Fields.COMMENTS_OWNER_ID, Fields.COMMENTS_CHILDREN_OWNER_ID);

        Map<String, String[]> nestedQueryTargets = new HashMap<>();
        nestedQueryTargets.put(Fields.COMMENTS_OWNER_ID, new String[] {Fields.COMMENTS_COMMENT_TEXT});
        nestedQueryTargets.put(Fields.COMMENTS_CHILDREN_OWNER_ID, new String[]{Fields.COMMENTS_CHILDREN_COMMENT_TEXT});

        SearchRequestByTargetUser request = new SearchRequestByTargetUser(userId, postingType, sinceId, maxId, count, targets, nestedQueryTargets, optionalQuery);
        return searchUserContents(request);
    }

    /**
     * posting Type 변경
     * @param postingIds 게시글ID 리스트
     * @param postingType 변경할 게시판 타입
     */
    @SuppressWarnings("unused")
    @Transactional
    public void updatePostingType(List<Long> postingIds, PostingType postingType) {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);

        //기존 검색엔진 인덱싱 제거
        for(Posting posting : postingRepository.findAllById(postingIds)) {
            fullTextEntityManager.purge(posting.getPostingType().getClazz(), posting.getId());
        }

        //dtype native 업데이트
        postingRepository.updateBoardType(postingIds, postingType.getDiscriminatorValue());

        //삭제된 인덱싱 추가.
        for(Posting posting : postingRepository.findAllById(postingIds)) {
            fullTextEntityManager.index(posting);
        }
    }

    private Posting createPostingObject(PostingType postingType) {
        if(postingType == null) {
            throw new FlabookGlobalException("postingType 이 없습니다.");
        }

        Posting posting = null;
        switch (postingType) {
            case question:
                posting = new Question();
                break;

            case boast:
            case boast_wait:
                posting = new Boast();
                break;

            case magazine:
                posting = new Magazine();
                break;

            case magazine_wait:
                posting = new MagazineWait();
                break;

            case clinic:
                posting = new Clinic();
                break;

            case free:
            case free_wait:
                posting = new Free();
                break;

            case guidebook:
                posting = new Guide();
                break;

            case television:
                posting = new Television();
                break;

            case television_wait:
                posting = new TelevisionWait();
                break;

            case photo:
                posting = new Photo();
                break;

            default:
                break;
        }
        return posting;
    }


    //
    private boolean validFiles(PostingType postingType, MultipartFile[] files) {
        if(files == null || files.length == 0) {
            return PostingType.free.equals(postingType) || PostingType.television.equals(postingType) || PostingType.television_wait.equals(postingType);
        }
        return true;
    }

    private List<Tag> findTagByLocation(Point point) {
        if(point != null) {
            Polygon circle = GeoUtils.getInstance().createCircle(point.getX(), point.getY());
            return tagRepository.findByLocation(circle);
        } else {
            return Collections.emptyList();
        }
    }

    private void addPlaceName(ImageUploadService.ImageResourceInfoWithMetadata meta) {
        Address response = kakaoOpenApiService.coord2Address(String.format("%.8f", meta.getPoint().getX()), String.format("%.8f", meta.getPoint().getY()));
        if (response.getMeta().getTotal_count() > 0 && StringUtils.isNotBlank(response.getDocuments().get(0).getAddress().getRegion_3depth_name())) {
            String[] arr = response.getDocuments().get(0).getAddress().getRegion_3depth_name().split(" ");
            meta.setPlaceName(arr[0]);
        }
    }

    private ImageUploadService.ImageResourceInfoWithMetadata upload(MultipartFile file, boolean resizing) throws ImageProcessingException, IOException, InterruptedException {
        return imageUploadService.uploadWithMeta(FolderDatePatterns.POSTINGS, file, resizing);
    }

    private final UnaryOperator<ImageUploadService.ImageResourceInfoWithMetadata> addPlaceNameFn = meta -> {
        if (meta.getPoint() == null) return meta;
        addPlaceName(meta);
        return meta;
    };

    private final BiFunction<MultipartFile, Boolean, ImageUploadService.ImageResourceInfoWithMetadata> uploadImageFn = (file, resizing) -> {
        try {
            return upload(file, resizing);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            throw new CompletionException(e);
        } catch (IOException | ImageProcessingException e) {
            e.printStackTrace();
            throw new CompletionException(e);
        }
    };


    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value=CacheValues.POSTING_TIMELINE, key = "{#vo.postingType.clazz.name, null, null, 10}"),
            @CacheEvict(value=CacheValues.POSTING_TIMELINE, key = "{'find', #vo.postingType.clazz.name, null, null, 10}")
    })
    public PostingDto createPosting(CreatePostVo vo, User owner) throws ExecutionException, InterruptedException {

        if (!validFiles(vo.getPostingType(), vo.getFiles())) {
            throw new FlabookGlobalException(FlabookGlobalException.Messages.PLEASE_SELECT_PHOTO);
        }

        Posting posting = createPostingObject(vo.getPostingType());
        Assert.notNull(posting, "unknown posting type : " + vo.getPostingType());
        posting.setOwner(owner);
        posting.setText(vo.getText());


        if(StringUtils.isNotBlank(vo.getYoutubeId())) {
            posting.setYoutubeId(vo.getYoutubeId());
        }

        if (vo.getPostingType().isAllowTitle()) {
            posting.setTitle(vo.getTitle());
        }

        int descriptionSize = (vo.getDescriptions() != null) ? vo.getDescriptions().length : 0;
        List<PostingAttachment> attachmentList = new ArrayList<>();
        List<CompletableFuture<ImageUploadService.ImageResourceInfoWithMetadata>> futureList = new ArrayList<>();

        if (vo.getFiles() != null) {
            for (MultipartFile file : vo.getFiles()) {
                if(file.isEmpty()) continue;

                futureList.add(CompletableFuture.completedFuture(uploadImageFn.apply(file, true)).thenApply(addPlaceNameFn));
            }
        }
        CompletableFuture<?>[] futures = futureList.toArray(new CompletableFuture[]{});
        CompletableFuture.allOf(futures).join();

        if (vo.getGoodsNos() != null && !vo.getGoodsNos().isEmpty()) {
            posting.setGoodses(goodsRepository.findAllById(vo.getGoodsNos().stream().map(String::trim).collect(Collectors.toList())));
        }

        String description = null;
        for (int j = 0; j < futures.length; j++) {
            if (vo.getPostingType().isAllowDescription() && descriptionSize > j) {
                description = vo.getDescriptions()[j];
            }

            ImageUploadService.ImageResourceInfoWithMetadata resourceWithMeta = (ImageUploadService.ImageResourceInfoWithMetadata) futures[j].get();
            List<Tag> tags = findTagByLocation(resourceWithMeta.getPoint());
            PostingAttachment attachment = PostingAttachment.builder().parent(posting)
                    .imageResource(resourceWithMeta.getImageResource())
                    .dimension(resourceWithMeta.getDimension())
                    .location(resourceWithMeta.getPoint())
                    .placeName(resourceWithMeta.getPlaceName())
                    .tags(new HashSet<>(tags))
                    .description(description).build();

            attachmentList.add(attachment);
        }

        userRepository.updateIncrementPostingCount(owner.getId());
        posting.setAttachments(attachmentList);

        if(posting instanceof IPoster) {
            List<PosterAttachment> posterInfos = new ArrayList<>();
            try {
                for (MultipartFile poster : vo.getPosters()) {
                    ImageUploadService.ImageResourceInfoWithMetadata resourceInfoWithMetadata = imageUploadService.uploadWithMeta(FolderDatePatterns.POSTINGS, poster);
                    posterInfos.add(PosterAttachment.builder().parent(posting)
                            .imageResource(resourceInfoWithMetadata.getImageResource())
                            .dimension(resourceInfoWithMetadata.getDimension()).build());
                }
            } catch (InterruptedException e) {
                log.error("createPosting InterruptedException ", e);
                Thread.currentThread().interrupt();
                throw new FlabookGlobalException(e.getMessage());
            } catch (IOException | ImageProcessingException e) {
                log.error("createPosting", e);
                throw new FlabookGlobalException(e.getMessage());
            } catch (NullPointerException e) {
                throw new FlabookGlobalException(FlabookGlobalException.Messages.PLEASE_SELECT_POSTER);
            }

            posting.setPosters(posterInfos);
        }

        if (posting instanceof Boast) {
            posting = postingRepository.saveAndFlush((Boast) posting);
        } else if (posting instanceof Magazine) {
            posting = postingRepository.saveAndFlush((Magazine) posting);
        } else if (posting instanceof Guide) {
            posting = postingRepository.saveAndFlush((Guide) posting);
        } else if (posting instanceof Television) {
            posting = postingRepository.saveAndFlush((Television) posting);
        } else if (posting instanceof Clinic) {
            Clinic clinic = (Clinic) posting;
            if(vo.getPlace() != null && vo.getLight() != null && vo.getWater() != null && vo.getRepotting() != null) {
                clinic.setCondition(new ClinicCondition(vo.getPlace(), vo.getLight(), vo.getWater(), vo.getRepotting()));
            }
            posting = postingRepository.saveAndFlush(clinic);
        } else {
            posting = postingRepository.saveAndFlush(posting);
        }

        return PostingDto.of(posting);
    }

    private String getDescriptionByIndex(UpdatePostVo vo, int i) {
        return (vo.getDescriptions() != null && vo.getDescriptions().length > i) ? vo.getDescriptions()[i] : StringUtils.EMPTY;
    }

    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
        @CacheEvict(value = CacheValues.POSTINGS, key = "#id")
    })
    public PostingDto updatePosting(Long id, UpdatePostVo vo, User user) throws InterruptedException, IOException, ImageProcessingException {

        Posting posting = postingRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException("not found entity : " + id));
        if(!AccessControl.isAllowed(posting.getOwner(), user) || posting.isDelete() || posting.isBlind()) {
            throw new FlabookPermissionException("권한이 없습니다.");
        }

        posting.setTitle(vo.getTitle() != null ? vo.getTitle() : StringUtils.EMPTY);
        posting.setText(vo.getText());
        int size  = (vo.getFiles() != null) ? vo.getFiles().length : 0;

        List<Modify> modifies = new ArrayList<>();
        for(int i = 0 ; i < size ; i++) {
            String description = (posting.getPostingType().isAllowDescription()) ? getDescriptionByIndex(vo, i) : null;
            modifies.add(
                new Modify((vo.getIds() != null && vo.getIds().length > i) ? vo.getIds()[i] : null
                , description
                , (vo.getFiles() != null) ? vo.getFiles()[i] : null, i));
        }

        //ID -> Seq
        Map<String, Integer> modifiesMap = modifies.stream().filter(modify -> modify.id != null && !modify.id.trim().isEmpty()).collect(Collectors.toMap(modify -> modify.id, Modify::getSeq));
        Set<String> liveIds = modifies.stream().map(Modify::getId).collect(Collectors.toSet());

        //form에 없는 id 는 삭제
        posting.getAttachments().removeIf(p -> !liveIds.contains(String.valueOf(p.getId())));

        posting.getAttachments().sort(Comparator.comparing(o -> modifiesMap.getOrDefault(String.valueOf(o.getId()), 99)));

        for(int i = 0 ; i < modifies.size() ; i++ ) {
            Modify modify = modifies.get(i);
            if(modify.id != null && !modify.id.trim().isEmpty()) {
                //이미 존재
                posting.getAttachments().get(i).setDescription(modify.getDescription());
            } else {
                //아이디가 없으면 신규
                ImageUploadService.ImageResourceInfoWithMetadata infoWithMetadata = imageUploadService.uploadWithMeta(FolderDatePatterns.POSTINGS, modify.getFile());
                PostingAttachment attachment = PostingAttachment.builder().parent(posting).imageResource(infoWithMetadata.getImageResource())
                        .dimension(infoWithMetadata.getDimension()).location(infoWithMetadata.getPoint())
                        .description(modify.getDescription()).build();
                posting.getAttachments().add(modify.seq, attachment);
            }
        }

        if(vo.getGoodsNos() != null && !vo.getGoodsNos().isEmpty()) {
            posting.getGoodses().clear();
            posting.getGoodses().addAll(goodsRepository.findAllById(vo.getGoodsNos().stream().map(String::trim).collect(Collectors.toList())));
        }

        if(posting instanceof IPoster) {
            try {
                List<PosterAttachment> posterInfos = new ArrayList<>();
                if (vo.getPosters() != null) {
	                for (MultipartFile poster : vo.getPosters()) {
	                    ImageUploadService.ImageResourceInfoWithMetadata resourceInfoWithMetadata = imageUploadService.uploadWithMeta(FolderDatePatterns.POSTINGS, poster);
	                    posterInfos.add(PosterAttachment.builder().parent(posting)
	                            .imageResource(resourceInfoWithMetadata.getImageResource())
	                            .dimension(resourceInfoWithMetadata.getDimension()).build());
	                }
                }

                if(!posterInfos.isEmpty()) {
                    if(posting.getPosters() != null) {
                        posting.getPosters().clear();
                    } else {
                        posting.setPosters(new ArrayList<>());
                    }
                    posting.getPosters().addAll(posterInfos);
                }

            } catch (InterruptedException e) {
                log.error("createPosting InterruptedException", e);
                Thread.currentThread().interrupt();
                throw new FlabookGlobalException(e.getMessage());
            } catch (IOException | ImageProcessingException e) {
                log.error("createPosting", e);
                throw new FlabookGlobalException(e.getMessage());
            } catch(NullPointerException e) {
                throw new FlabookGlobalException(FlabookGlobalException.Messages.PLEASE_SELECT_POSTER);
            }

        } else if(PostingType.clinic.equals(posting.getPostingType())) {
            Clinic clinic = (Clinic) posting;
            if(vo.getPlace() != null && vo.getLight() != null && vo.getWater() != null && vo.getRepotting() != null) {
                clinic.setCondition(new ClinicCondition(vo.getPlace(), vo.getLight(), vo.getWater(), vo.getRepotting()));
            }
        }

        posting = postingRepository.save(posting);
        return PostingDto.of(posting);
    }


    /**
     * 검색엔진 색인
     * @param posting 게시글
     */
    @SuppressWarnings("unused")
    private void indexing(Posting posting) {
        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
        FullTextSession session = fullTextEntityManager.unwrap(FullTextSession.class);
        log.info("indexing count : {}", posting.getCommentCount());
        session.index(posting);
        session.flush();
        session.clear();
    }

    @Transactional
    public boolean indexing(List<Long> ids, Integer max) {
        log.info("indexing : {}", ids);
        List<Posting> postings = postingRepository.findAllById(ids);
        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
        FullTextSession session = fullTextEntityManager.unwrap(FullTextSession.class);

        for(Posting posting : postings) {
            if(max != null && posting.getCommentCount() > max) {
                continue;
            }
            session.index(posting);
        }
        session.flushToIndexes();
        session.clear();

        return true;
    }

    private void validateCommentForm(CreateCommentVo vo ) {
        if((vo.getText() == null || vo.getText().isEmpty()) && (vo.getFiles() == null || vo.getFiles().isEmpty())) {
            throw new FlabookGlobalException("내용이나 사진이 포함되지 않았습니다.");
        } else {
            if(vo.getText().length() > Comment.MAX_TEXT_LENGTH) {
                throw new FlabookGlobalException("내용의 길이는 2048자로 제한됩니다.");
            }
        }
    }

    /**
     *
     * 게시글이 임시등록 (wait) 이고 사용자가 관리자(admin, expert) 가 아닐 경우 댓글을 등록할 수 없음
     *
     * @param posting 게시글
     * @param user 접근사용자
     */
    public static void validateAccessPosting(Posting posting, User user) {
        if(posting.getPostingType().name().toLowerCase().contains("wait") && !(user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.EXPERT )) {
            throw new FlabookPermissionException(FlabookPermissionException.Messages.NOT_AUTHORIZED);
        }
    }

    private void validateCreateReplyForm(CreateCommentVo vo) {
        if((vo.getText() == null || vo.getText().isEmpty()) && (vo.getFiles() == null || vo.getFiles().isEmpty())) {
            throw new FlabookGlobalException("text 나 files 가 포함되지 않음");
        }
    }

    @Caching(evict = {
            @CacheEvict(value = CacheValues.POSTINGS, key = "#id"),
            @CacheEvict(value = CacheValues.COMMENT_LIST, key = "{#id}", condition = "#result.postingId != null"),
            @CacheEvict(value = CacheValues.COMMENT_LIST, key = "{#id, 'adopt'}"),
            @CacheEvict(value = CacheValues.COMMENT_LIST, key = "{#id, 'first'}")
    })
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public CommentDto createComment(long id, CreateCommentVo vo, User owner) throws FlabookGlobalException, IOException, InterruptedException, ImageProcessingException {

        validateCommentForm(vo);

        Posting posting = postingRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY + id));
        validateNotPhoto(posting);
        validateAccessPosting(posting, owner);

        postingRepository.updateCommentCount(id, 1);
        postingRepository.updateReadCount(id, 1);

        Comment comment = Comment.builder().owner(owner).posting(posting).text(vo.getText()).build();
        if (vo.getFiles() != null && !vo.getFiles().isEmpty()) {
            ImageUploadService.ImageResourceInfoWithMetadata infoWithMetadata = imageUploadService.uploadWithMeta(FolderDatePatterns.COMMENTS, vo.getFiles());

            CommentAttachment attachment = CommentAttachment.builder().parent(comment)
                    .imageResource(infoWithMetadata.getImageResource())
                    .dimension(infoWithMetadata.getDimension()).location(infoWithMetadata.getPoint())
                    .build();
            comment.setAttachments(Collections.singletonList(attachment));
        }

        //언급된 사용자

        //댓글알림수신자
        List<User> recipients = new ArrayList<>();

        //언급알림수신자
        List<User> mentionRecipients = new ArrayList<>();

        //댓글 알림 수신은 작성자만 하고, 언급이 있을 경우 언급된 사용자만 받는다.
        List<Long> mentionIds = MentionUtils.extractMentions(vo.getText());
        if(!mentionIds.isEmpty()) {
            List<User> mentions = new ArrayList<>(commentRepository.findDistinctRecipientByMentionUserIds(comment.getPosting().getId(), mentionIds));
            comment.setMentions(mentions.stream().distinct().collect(Collectors.toList()));

            mentionRecipients.addAll(mentions);

            //게시글 작성자를 업급한 경우 댓글 알림, 언급 알림 차단에 따른 분기
            boolean isMentionOwner = mentionIds.contains(posting.getOwner().getId());

            /*
             *게시물 작성자의 이름을 언급하는 댓글이 달릴 경우 ( 굳이 게시물 작성자에 대한 언급을 하면서 게시글에 댓글을 달 경우) 다음과 같은 2가지 경우에 알림이 가야합니다.
             * 게시글 작성자가 댓글 알림 받기, 언급 알림 받지 않기 설정한 경우
             * 게시글 작성자가 댓글 알림 받지 않기, 언급 알림 받기 설정한 경우
             */
            if(isMentionOwner) {
                if(posting.getOwner().getUserSetting() != null && posting.getOwner().getUserSetting().isPostingNotiEnable()) {
                    mentionRecipients.removeIf(user -> user.getId().equals(posting.getOwner().getId()));
                    recipients.add(posting.getOwner());
                } else {
                    mentionRecipients.add(posting.getOwner());
                }
            }

        } else {
            recipients.add(posting.getOwner());
            //참여한 게시글의 모든 댓글알림을 받고 싶은 사용자
            List<User> commentUsers = commentRepository.findDistinctJoinPushEnabledRecipientList(posting.getId());
            for(User commentUser : commentUsers) {
                if(commentUser.getUserSetting().isCommentNotiEnable()) {
                    recipients.add(commentUser);
                }
            }
        }

        //작성된 댓글이 있는지 확인
        Integer commentCount = commentRepository.countByPostingIdAndOwnerId(id, owner.getId());
        comment = commentRepository.saveAndFlush(comment);

        if (commentCount == 0 && !owner.equals(posting.getOwner())) {
            userService.updateLevel(owner, owner.getActivity().getCommentCount() + 1);
            userRepository.updateIncrementCommentCount(owner.getId());
        }

        notificationService.afterNewMention(comment, mentionRecipients);
        notificationService.afterNewComment(comment, recipients);

        return CommentDto.of(comment).setPostingId(id).setPostingType(posting.getPostingType()).setFirstComment(posting.getCommentCount() == 0);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheValues.POSTINGS, key = "#id"),
            @CacheEvict(value = CacheValues.COMMENT_LIST, key = "{#result.postingId}", condition = "#result.postingId != null"),
            @CacheEvict(value = CacheValues.COMMENT_LIST, key = "{#result.postingId, 'adopt'}"),
            @CacheEvict(value = CacheValues.COMMENT_LIST, key = "{#result.postingId, 'first'}")
    })
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public CommentDto createReply(long id, @Valid CreateCommentVo vo, User owner, boolean isThanks, boolean isAdopt) throws FlabookGlobalException, IOException, InterruptedException, ImageProcessingException {

        validateCreateReplyForm(vo);

        Comment comment = commentRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY + id));

        Comment parent = (comment.getParent() != null) ? comment.getParent() : comment;
        Long postingId = comment.getPosting().getId();
        Posting posting = postingRepository.findLockOnly(postingId).orElseThrow(() -> new RuntimeException("not found posting : " + comment.getPosting().getId()));
        validateNotPhoto(posting);
        validateAccessPosting(posting, owner);

        postingRepository.updateCommentCount(postingId, 1);
        postingRepository.updateReadCount(postingId, 1);
        Comment.CommentBuilder replyBuilder = Comment.builder().owner(owner).text(vo.getText()).parent(parent).posting(posting);
        Comment reply = replyBuilder.build();

        if (vo.getFiles() != null && !vo.getFiles().isEmpty()) {
            ImageUploadService.ImageResourceInfoWithMetadata infoWithMetadata = imageUploadService.uploadWithMeta(FolderDatePatterns.COMMENTS, vo.getFiles());
            CommentAttachment attachment = CommentAttachment.builder().parent(reply)
                    .imageResource(infoWithMetadata.getImageResource())
                    .location(infoWithMetadata.getPoint())
                    .dimension(infoWithMetadata.getDimension())
                    .build();
            reply.setAttachments(Collections.singletonList(attachment));
        }

        List<User> recipients = new ArrayList<>();
        List<User> mentionRecipients = new ArrayList<>();

        //댓글 알림 수신은 작성자만 하고, 언급이 있을 경우 언급된 사용자만 받는다.

        List<Long> mentionIds = MentionUtils.extractMentions(vo.getText());
        if(!mentionIds.isEmpty()) {
            List<User> mentions = new ArrayList<>(commentRepository.findDistinctRecipientByMentionUserIds(postingId, mentionIds));
            reply.setMentions(mentions.stream().distinct().collect(Collectors.toList()));
            mentionRecipients = new ArrayList<>(mentions);
            boolean isMentionOwner = mentionIds.contains(comment.getOwner().getId());
            //1. 작성자가 언급되면 언급이 아닌 답글 알림으로 보냄
            if(isMentionOwner) {
                mentionRecipients.remove(comment.getOwner());
                recipients.add(comment.getOwner());
            }

        } else {
            recipients.add(comment.getOwner());
        }

        //작성된 댓글이 있는지 확인
        Integer commentCount = commentRepository.countByPostingIdAndOwnerId(id, owner.getId());

        commentRepository.saveAndFlush(reply);

        if (commentCount == 0 && !owner.equals(posting.getOwner())) {
            //첫 답글일때만 업데이트
            userService.updateLevel(owner, owner.getActivity().getCommentCount() + 1);
            userRepository.updateIncrementCommentCount(owner.getId());
        }

        if(isAdopt) {

            if(adoptCommentActivityRepository.existsByPostingId(posting.getId())) {
                throw new FlabookGlobalException("이미 채택된 답변이 존재합니다.");
            }

            userRepository.updateIncrementAdoptedCount(comment.getOwner().getId());
            posting.setAdopt(true);
            comment.setAdopt(true);
            AdoptComment adoptComment = AdoptComment.builder()
                    .relation(new UserPostingRelation(comment.getOwner(), posting))
                    .target(new UserCommentRelation(comment.getOwner(), comment))
                    .source(new UserCommentRelation(owner, reply)).build();
            adoptCommentActivityRepository.save(adoptComment);
            notificationService.afterNewReply(adoptComment, recipients);
        }

        if(isThanks) {
            ThanksComment thanksComment = ThanksComment.builder()
                    .relation(new UserPostingRelation(comment.getOwner(), posting))
                    .target(new UserCommentRelation(comment.getOwner(), comment))
                    .source(new UserCommentRelation(owner, reply)).build();
            thanksCommentActivityRepository.save(thanksComment);
            notificationService.afterNewReply(thanksComment, recipients);
        }

        if(!isAdopt && !isThanks) {
            notificationService.afterNewReply(reply, recipients);
            notificationService.afterNewMention(reply, mentionRecipients);
        }

        return CommentDto.of(reply).setTargetUserId(targetUserId(comment, isAdopt, isThanks)).setPostingId(posting.getId()).setType(commentType(isAdopt, isThanks)).setPostingType(posting.getPostingType());
    }

    private static String commentType(boolean isAdopt, boolean isThanks) {
        if(isAdopt) {
            return "adopt";
        } else if(isThanks) {
            return "thanks";
        }
        return "reply";
    }

    @SuppressWarnings("unused")
    private static Long targetUserId(Comment parent, boolean isAdopt, boolean isThanks) {
        if(isAdopt) {
           return parent.getOwner().getId();
        }

        return null;
    }

    @Caching(evict = {
        @CacheEvict(value = CacheValues.POSTINGS, key = "#result.postingId"),
        @CacheEvict(value = CacheValues.COMMENT_LIST, key = "{#result.postingId}", condition = "#result.postingId != null")
    })
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public CommentDto deleteComment(long id, User user) throws FlabookGlobalException {

        Comment comment = commentRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException("not found comment entity :" + id));
        if (comment.isDelete()) {
            throw new FlabookGlobalException("not found comment entity : " + id);
        }

        long postingId;
        if (comment.getPosting() != null && comment.getPosting().getId() != null) {
            postingId = comment.getPosting().getId();
        } else {
            postingId = comment.getParent().getPosting().getId();
        }

        if (!AccessControl.isAllowed(comment.getOwner(), user)) {
            throw new FlabookPermissionException("인증되지 않음");
        }

        Posting posting = postingRepository.findLockOnly(postingId).orElseThrow(() -> new FlabookGlobalException("not found comment entity :" + id));
        postingRepository.updateCommentCount(postingId, -1);
        comment.setDelete(true);

        Integer commentCount = commentRepository.countByPostingIdAndOwnerId(postingId, comment.getOwner().getId());
        commentRepository.saveAndFlush(comment);

        log.info("deleteComment id : {}, commentCount : {}, owner : {}", id, commentCount, comment.getOwner().getId());
        if (commentCount == 1 && !comment.getOwner().equals(posting.getOwner())) {
            //답글이 없으면 사용자 답글수 삭제
            userRepository.updateDecrementCommentCount(comment.getOwner().getId());
        }

        return CommentDto.of(comment).setPostingId(postingId);
    }

    private QueryRequest requestToQueryRequest(SearchTimelineRequest request) {
        Optional<ZonedDateTime> optionalFrom = request.getOptionalDays().map(day -> ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).minusDays(day));
        return new QueryRequest(request.getUser(), request.getClazz(), request.getSinceId(), request.getMaxId(), request.getQuery(), Fields.ID, null, request.isMine(), optionalFrom, Optional.empty(), request.getOptionalBelowCommentCount(), request.getOptionalQueryTargetFields());
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    @Cacheable(value = CacheValues.POSTING_TIMELINE, key = "{#request.clazz.name, #request.sinceId, #request.maxId, #request.count}", condition = "#request.query == null && #request.mine == false && #request.optionalBelowCommentCount.isPresent() == false && #request.optionalDays.isPresent() == false")
    public List<PostingDto> searchTimeline(SearchTimelineRequest request) {

        QueryRequest queryRequest = requestToQueryRequest(request);
        FullTextQuery fullTextQuery  = searchQuery(queryRequest);
        fullTextQuery.setFirstResult(0);
        fullTextQuery.setMaxResults(request.getCount());

        List<Posting> postings = fullTextQuery.getResultList();
        return postings.stream().map(PostingDto::of).collect(Collectors.toList());
    }

    @SuppressWarnings("unused")
    @Transactional(readOnly = true)
    @Cacheable(value = CacheValues.POSTING_SEARCH_COUNT, key = "{#request.clazz.name, #request.query}", condition = "#request.mine == false && #request.optionalBelowCommentCount.isPresent() == false")
    public int searchResultCount(SearchTimelineRequest request) {
        QueryRequest queryRequest = requestToQueryRequest(request);
        FullTextQuery fullTextQuery = createQuery(queryRequest);
        return fullTextQuery.getResultSize();
    }


    @SuppressWarnings("unused")
    @Transactional(readOnly = true)
    @Cacheable(value = CacheValues.POSTING_TIMELINE, key = "{'find', #clazz.name, #sinceId, #maxId, #count}")
    public List<PostingDto> findTimeline(User user, Class<? extends Posting> clazz, Long sinceId, Long maxId, int count) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<?> query = cb.createQuery(clazz);

        List<Predicate> predicates = new ArrayList<>();
        Root<? extends Posting> root = query.from(clazz);

        if(sinceId != null && !sinceId.equals(0L)) {
            predicates.add(cb.gt(root.get(Fields.ID), sinceId));
        }

        if(maxId != null && !maxId.equals(0L)) {
            predicates.add(cb.le(root.get(Fields.ID), maxId));
        }

        query.where(predicates.toArray(new Predicate[]{})).orderBy(cb.desc(root.get(Fields.ID)));
        return em.createQuery(query).setMaxResults(count).getResultList().stream().map(o -> PostingDto.of((Posting)o)).collect(Collectors.toList());
    }

    /*
     * 최신 컨텐츠 기반으로 정렬
     */
    @SuppressWarnings({"unchecked", "unused"})
    @Transactional(readOnly = true)
    @Cacheable(value = CacheValues.HOME, key = "{#clazz.name, #offset, #count}")
    public List<PostingDto> home(User user, Class<? extends Posting> clazz, int offset, int count) {

        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);

        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(clazz)
                .get();

        BooleanJunction<?> booleanJunction = qb.bool();
        booleanJunction.must(qb.keyword().onField(Fields.IS_DELETE).matching(true).createQuery()).not();

        Query luceneQuery = booleanJunction.createQuery();

        Sort sort = qb.sort().byScore().desc().createSort();

        javax.persistence.Query persistenceQuery =
                fullTextEntityManager.createFullTextQuery(luceneQuery, clazz)
                        .setFirstResult(offset)
                        .setMaxResults(count).setSort(sort);

        List<Posting> postings = persistenceQuery.getResultList();
        return postings.stream().map(PostingDto::of).collect(Collectors.toList());
    }

    /*
     * 최신 컨텐츠 기반으로 정렬
     */
    @SuppressWarnings(value = "unchecked")
    @Transactional(readOnly = true)
    @Cacheable(value = CacheValues.HOME, key = "{#request.postingType, #request.offset, #request.count, #request.orderBy, #request.sortBy, #request.optionalBelowCommentCount, #request.optionalDays}", condition = "#request.query == null && #request.mine == false")
    public List<PostingDto> search(SearchPageRequest request) {
        Optional<ZonedDateTime> optionalFrom = request.getOptionalDays().map(day -> ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).minusDays(day));
        QueryRequest queryRequest = new QueryRequest(request.getUser(), request.getPostingType().getClazz(), null, null, request.getQuery(), request.getOrderBy(), request.getSortBy(), request.isMine(), optionalFrom, Optional.empty(), request.getOptionalBelowCommentCount(), request.getOptionalQueryTargetFields());
        javax.persistence.Query persistenceQuery = searchQuery(queryRequest);
        persistenceQuery.setFirstResult(request.getOffset());
        persistenceQuery.setMaxResults(request.getCount());
        List<Posting> postings = persistenceQuery.getResultList();
        return postings.stream().map(PostingDto::of).collect(Collectors.toList());
    }

    @Data
    @Builder
    private static class PostingSearchBuilder {

        @NonNull
        private QueryBuilder queryBuilder;

        @Builder.Default
        @NonNull
        private List<Query> list = new ArrayList<>();
        private Long sinceId;
        private Long maxId;

        @Builder.Default
        private Boolean ignoreDeleted = true;

        public void addPredicate(Query query) {
            this.list.add(query);
        }

        public Query build() {
            BooleanJunction<?> where = queryBuilder.bool();

            if (sinceId != null && sinceId > 0) {
                where.must(NumericRangeQuery.newLongRange(Fields.ID, sinceId, Long.MAX_VALUE, false, true));
            }

            if (maxId != null && maxId > 0) {
                where.must(NumericRangeQuery.newLongRange(Fields.ID, Long.MIN_VALUE, maxId, true, true));
            }

            if(ignoreDeleted != null && ignoreDeleted) {
                where.must(queryBuilder.keyword().onField(Fields.IS_DELETE).matching(true).createQuery()).not();
            }

            for(Query query : list) {
                where.must(query);
            }

            if(where.isEmpty()) {
                where.must(queryBuilder.all().createQuery());
            }

            return where.createQuery();
        }
    }



    /**
     * Guide 는 default 가 asc
     * @param clazz Board
     * @param sortBy 정렬기준필드
     * @return asc, desc
     */
    private String getSortByOrDefault(Class<? extends Posting> clazz, String sortBy) {

        if(sortBy == null || sortBy.isEmpty()) {
            return (clazz == Guide.class) ? "asc" : "desc";
        } else {
            return sortBy;
        }
    }

    @SuppressWarnings("java:S107")
    @Getter
    @NoArgsConstructor
    private static class QueryRequest {
        private User user;
        private  Class<? extends Posting> clazz;
        private Long sinceId;
        private Long maxId;
        private String query;
        private String orderBy;
        private String sortBy;
        private boolean mine;
        private Optional<ZonedDateTime> optionalFrom;
        private Optional<ZonedDateTime> optionalTo;
        private Optional<Integer> optionalBelowCommentCount;
        //검색할 필드를 지정한다
        private Optional<String> optionalQueryTargetFields;

        public QueryRequest(User user, Class<? extends Posting> clazz, Long sinceId, Long maxId, String query, String orderBy, String sortBy, boolean mine, Optional<ZonedDateTime> optionalFrom, Optional<ZonedDateTime> optionalTo, Optional<Integer> optionalBelowCommentCount) {
            this.user = user;
            this.clazz = clazz;
            this.sinceId = sinceId;
            this.maxId = maxId;
            this.query = query;
            this.orderBy = orderBy;
            this.sortBy = sortBy;
            this.mine = mine;
            this.optionalFrom = optionalFrom;
            this.optionalTo = optionalTo;
            this.optionalBelowCommentCount = optionalBelowCommentCount;
            this.optionalQueryTargetFields = Optional.empty();
        }

        public QueryRequest(User user, Class<? extends Posting> clazz, Long sinceId, Long maxId, String query, String orderBy, String sortBy, boolean mine, Optional<ZonedDateTime> optionalFrom, Optional<ZonedDateTime> optionalTo, Optional<Integer> optionalBelowCommentCount, Optional<String> optionalQueryTargetFields) {
            this.user = user;
            this.clazz = clazz;
            this.sinceId = sinceId;
            this.maxId = maxId;
            this.query = query;
            this.orderBy = orderBy;
            this.sortBy = sortBy;
            this.mine = mine;
            this.optionalFrom = optionalFrom;
            this.optionalTo = optionalTo;
            this.optionalBelowCommentCount = optionalBelowCommentCount;
            this.optionalQueryTargetFields = optionalQueryTargetFields;
        }
    }

    private List<String> getSortFields(String orderBy) {
        List<String> sortFields;
        if ("popular".equalsIgnoreCase(orderBy)) {
            sortFields = Arrays.asList(Fields.LIKE_COUNT, Fields.COMMENT_COUNT, Fields.READ_COUNT, Fields.ID);
        } else if ("mostComments".equalsIgnoreCase(orderBy)) {
            sortFields = Arrays.asList(Fields.COMMENT_COUNT, Fields.ID);
        } else {
            sortFields = Collections.singletonList(orderBy);
        }
        return sortFields;
    }

    private Sort sortScore(SortContext sortContext, String sortBy) {
        if("desc".equals(sortBy)) {
            return sortContext.byScore().desc().createSort();
        } else {
            return sortContext.byScore().asc().createSort();
        }
    }

    private Sort sort(SortContext sortContext, String orderBy, String sortBy) {
        SortFieldContext sortFieldContext = null;

        if("score".equals(orderBy)) {
            return sortScore(sortContext, sortBy);
        }

        List<String> sortFields = getSortFields(orderBy);
        if("desc".equals(sortBy)) {
            for(String sortField : sortFields) {
                sortFieldContext = (sortFieldContext != null) ? sortFieldContext.andByField(sortField).desc() : sortContext.byField(sortField).desc();
            }
        } else {
            for(String sortField : sortFields) {
                sortFieldContext = (sortFieldContext != null) ? sortFieldContext.andByField(sortField).asc() : sortContext.byField(sortField).asc();
            }
        }

        return (sortFieldContext != null) ? sortFieldContext.createSort() : null;
    }

    private FullTextQuery createQuery(final QueryRequest request) {

        List<String> targetList = request.getOptionalQueryTargetFields()
            .filter(StringUtils::isNotBlank)
            .map(targets -> StringUtils.split(targets, ","))
            .map(Arrays::asList)
            .orElseGet(() -> {
                try {
                    return matchFields.getOrDefault(PostingType.findByEntityClazz(request.getClazz()), matchFields.get(PostingType.question));
                } catch (NotFoundException e) {
                    e.printStackTrace();
                    log.error("not found postingType", e);
                    return Collections.emptyList();
                }
            });
        return createQuery(request, targetList);
    }

    private FullTextQuery createQuery(QueryRequest request, List<String> targetList) {

        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(request.getClazz())
                .get();

        PostingSearchBuilder builder = PostingSearchBuilder.builder().queryBuilder(qb).build();

        if (Fields.ID.equalsIgnoreCase(request.getOrderBy())) {
            builder.setSinceId(request.getSinceId());
            builder.setMaxId(request.getMaxId());
        }

        if(request.isMine()) {
            builder.addPredicate(qb.keyword().onField(Fields.OWNER_ID).matching(request.getUser().getId()).createQuery());
        }

        if (request.getQuery() != null && request.getQuery().length() > 0) {
            BooleanJunction<?> keywordQuery = qb.bool();

            keywordQuery.should(qb.keyword().onFields(targetList.toArray(new String[]{})).matching(request.getQuery()).createQuery());

            builder.addPredicate(keywordQuery.createQuery());
        }

        request.getOptionalFrom().ifPresent(from -> builder.addPredicate(qb.range().onField(Fields.CREATED_AT).above(from).createQuery()));
        request.getOptionalTo().ifPresent(to -> builder.addPredicate(qb.range().onField(Fields.CREATED_AT).below(to).createQuery()));
        request.getOptionalBelowCommentCount().ifPresent(belowCommentCount -> builder.addPredicate(qb.range().onField(Fields.COMMENT_COUNT).below(belowCommentCount).createQuery()));

        builder.setIgnoreDeleted(true);
        Query luceneQuery = builder.build();

        String sortBy = getSortByOrDefault(request.getClazz(), request.getSortBy());
        Sort sort = sort(qb.sort(), request.getOrderBy(), sortBy);
        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, request.getClazz());
        if(sort != null) {
            fullTextQuery.setSort(sort);
        }
        return fullTextQuery;
    }

    private List<String> getTargetListByPosting(Posting posting) {

        if(posting instanceof Clinic && StringUtils.isNotBlank(posting.getTitle())) {
            // 품종(제목)이 있는 경우 동일 품목으로 검색
            return Collections.singletonList(Fields.TITLE);
        }

        return matchFields.getOrDefault(posting.getPostingType(), matchFields.get(PostingType.question));
    }

    private Set<String> getTagsByPosting(Posting posting) {
        Set<String> tags = new HashSet<>();
        if (posting instanceof Question) {
            //최근 답변에서 확인
            List<Comment> comments = commentRepository.findListByPostingId(posting.getId(), PageRequest.of(0, 3));
            for (Comment comment : comments) {
                Matcher m = pattern.matcher(comment.getText());
                while (m.find()) {
                    String hashTag = m.group();
                    tags.add(hashTag);
                }
            }
        } else if(posting instanceof Clinic) {
            //삭물명으로 검색
            if(StringUtils.isNotBlank(posting.getTitle())) {
                tags.add(posting.getTitle());
            }
        } else {
            String[] splitStr = posting.getText().split("\\s");
            tags.addAll(Arrays.asList(splitStr));
        }
        return tags;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    @Cacheable(value = CacheValues.SIMILAR_CONTENT_LIST, key = "{#id}", condition = "#offset == 0 && #count == 5 && #orderBy == 'score' && #sortBy == 'desc'")
    public List<PostingDto> findSimilarContentList(long id, int offset, int count, String orderBy, String sortBy) {

        Optional<Posting> optionalPosting = postingRepository.findById(id);
        if(!optionalPosting.isPresent())
            return Collections.emptyList();

        Posting posting = optionalPosting.get();
        Set<String> tags = getTagsByPosting(posting);
        List<String> targetList = getTargetListByPosting(posting);

        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(posting.getClass())
                .get();

        BooleanJunction<?> booleanJunction = qb.bool();
        PostingSearchBuilder builder = PostingSearchBuilder.builder().queryBuilder(qb).build();
        builder.setIgnoreDeleted(true);

        BooleanJunction<?> keywordQuery = qb.bool();
        for(String target : targetList ) {
            for (String str : tags) {
                keywordQuery.should(qb.keyword().onField(target).matching(str).createQuery());
            }
        }

        sortBy = getSortByOrDefault(posting.getClass(), sortBy);
        Sort sort = sort(qb.sort(), orderBy, sortBy);
        if(sort == null) {
            sort = qb.sort().byScore().desc().createSort();
        }

        builder.addPredicate(keywordQuery.createQuery());
        builder.addPredicate(booleanJunction.must(qb.keyword().onField(Fields.ID).matching(id).createQuery()).not().createQuery());

        javax.persistence.Query persistenceQuery =
                fullTextEntityManager.createFullTextQuery(builder.build(), posting.getClass())
                        .setFirstResult(offset).setMaxResults(count).setSort(sort);
        return ((List<Posting>) persistenceQuery.getResultList()).stream().map(PostingDto::of).collect(Collectors.toList());
    }

    @SuppressWarnings("unused")
    public void listWithLike(final List<PostingDto> list, User owner) {
        List<Long> ids = list.stream().map(PostingDto::getId).collect(Collectors.toList());
        final Map<Long, Boolean> likes = likePostingService.findListByUser(ids, owner);

        list.forEach(postingDto -> postingDto.setIsLike(likes.getOrDefault(postingDto.getId(), false)));
    }

    @SuppressWarnings("unused")
    public void listWithScrap(final List<PostingDto> list, User owner) {
        List<Long> ids = list.stream().map(PostingDto::getId).collect(Collectors.toList());
        final Map<Long, Boolean> scraps = scrapService.findListByUser(ids, owner);

        list.forEach(postingDto -> postingDto.setIsScrap(scraps.getOrDefault(postingDto.getId(), false)));
    }

    @SuppressWarnings("unused")
    public void listWithActivity(final List<PostingDto> list, User owner) {
        List<Long> ids = list.stream().map(PostingDto::getId).collect(Collectors.toList());
        final Map<Long, Boolean> scraps = scrapService.findListByUser(ids, owner);
        final Map<Long, Boolean> likes = likePostingService.findListByUser(ids, owner);
        list.forEach(postingDto -> {
            postingDto.setIsLike(likes.getOrDefault(postingDto.getId(), false));
            postingDto.setIsScrap(scraps.getOrDefault(postingDto.getId(), false));
        });
    }

    @SuppressWarnings("unused")
    public void listWithAnswer(final List<PostingDto> list) {
        List<Long> postingIds = list.stream().filter(p -> p.getCommentCount() > 0 && p.getCommentCount() <= 30 ).map(PostingDto::getId).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(postingIds)) {
            Map<Long, AnswerDto> answers = commentService.findFirstHashtags(postingIds).stream().collect(Collectors.toMap(AnswerDto::getPostingId, answerDto -> answerDto));
            list.forEach(p -> p.setAnswer(answers.get(p.getId())));
        }
    }

    @SuppressWarnings("unused")
    private List<User> findRecipientList(long postingId) {
        return commentRepository.findDistinctRecipientWithoutReplyList(postingId);
    }

    @Transactional
    @CacheEvict(value = CacheValues.POSTINGS, key = "#id")
    public PostingDto deletePosting(Long id, User user) throws FlabookGlobalException {
        Posting posting = findPostingLockMode(id, user);

        if(posting.getCommentCount() > 0) {
            throw new FlabookGlobalException(FlabookGlobalException.Messages.DO_NOT_DELETE_COMMENT);
        }

        userRepository.updateDecrementPostingCount(posting.getOwner().getId());
        posting.setDelete(true);
        return PostingDto.of(posting);
    }

    /**
     * 게시글 어드민 삭제
     * @param id 게시글ID
     * @param user 요청자
     * @return PostingDto
     * @throws FlabookGlobalException 예외
     */
    @SuppressWarnings("unused")
    @Transactional
    @CacheEvict(value = CacheValues.POSTINGS, key = "#id")
    public PostingDto forceDeletePosting(Long id, User user) throws FlabookGlobalException {
        Posting posting = postingRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY  + id));
        if(!AccessControl.isAllowed(posting.getOwner(), user)) {
            throw new FlabookPermissionException(FlabookPermissionException.Messages.NOT_AUTHORIZED);
        }

        List<User> commentOwners = commentRepository.findDistinctRecipientList(id);
        for(User commentOwner : commentOwners) {
            if(posting.getOwner().equals(commentOwner))
                continue;

            userRepository.updateDecrementCommentCount(commentOwner.getId());
        }

        userRepository.updateDecrementPostingCount(posting.getOwner().getId());
        posting.setDelete(true);
        return PostingDto.of(posting);
    }

    @SuppressWarnings("unused")
    @Transactional
    @CacheEvict(value = CacheValues.POSTINGS, key = "#id")
    public PostingDto blindPosting(Long id, User user, boolean isBlind) throws FlabookGlobalException {
        Posting posting = findPostingLockMode(id, user);
        posting.setBlind(isBlind);
    	return PostingDto.of(posting);
    }

    private Posting findPostingLockMode(Long id, User user) {
        Posting posting = postingRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY + id));
        if(!AccessControl.isAllowed(posting.getOwner(), user)) {
            throw new FlabookPermissionException(FlabookPermissionException.Messages.NOT_AUTHORIZED);
        }

        if (posting.isDelete()) {
            throw new FlabookGlobalException(FlabookGlobalException.Messages.DELETED_POSTING);
        }

        return posting;
    }

    @SuppressWarnings("unused")
    @Transactional
    @CacheEvict(value = CacheValues.COMMENT_LIST, key = "{#result.postingId}")
    public CommentDto blindComment(Long id, User user, boolean isBlind) throws FlabookGlobalException {
        Comment comment = commentRepository.findLockOnly(id).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY + id));
        if(!AccessControl.isAllowed(comment.getOwner(), user)) {
            throw new FlabookPermissionException(FlabookPermissionException.Messages.NOT_AUTHORIZED);
        }

        log.info("REACHED 3rd -> comment blinding");
        if (comment.isDelete()) {
            throw new FlabookGlobalException(FlabookGlobalException.Messages.DELETED_POSTING);
        }

        comment.setBlind(isBlind);
        return CommentDto.of(comment).setPostingId(comment.getPosting().getId());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheValues.MENTION_USERS, key = "{#id, #query}")
    public List<UserIdWithNicknameDto> findPostingMentionUsers(long id, String query) {
        return commentRepository.findDistinctRecipientList(id, query.replace("@", ""), org.springframework.data.domain.Sort.by("owner.nickname").ascending()).stream().map(UserIdWithNicknameDto::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostingActivityDto findPostingActivity(long id, User user) {
        PostingActivityDto activity = new PostingActivityDto();
        activity.setIsLike(likePostingService.isLikePost(id, user));

        //알림 설정은 항상 최우선 확인
        Boolean isWatch = watchPostingService.isWatchPost(id, user);
        if(isWatch == null) {
            isWatch = !commentRepository.findDistinctRecipientByMentionUserIds(id, Collections.singletonList(user.getId())).isEmpty();
            isWatch = isWatch || postingRepository.isPostingOwner(id, user.getId());
        }
        activity.setIsWatch(isWatch);
        activity.setIsScrap(scrapService.isScrap(id, user));
        return activity;
    }

    /**
     * 게시글 연관상품 목록
     * @param id 게시글ID
     * @return List<GoodsDto>
     */
    @Cacheable(value = CacheValues.RELATION_GOODSES, key = "{#id}")
    public List<GoodsDto> findRelationGoodses(long id) {
        return postingRepository.findRelationGoodses(id).stream().map(GoodsDto::of).collect(Collectors.toList());
    }

    private FullTextQuery searchQuery(QueryRequest request) {
        return createQuery(request);
    }

    /**
     * photo 게시글 생성
     * */
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value=CacheValues.POSTING_TIMELINE, key = "{#vo.postingType.clazz.name, null, null, 10}"),
            @CacheEvict(value=CacheValues.POSTING_TIMELINE, key = "{'find', #vo.postingType.clazz.name, null, null, 10}"),
            @CacheEvict(value = "photoAlbum", key = "#currentUser.id")
    })
    public List<PostingDto> createPhotoPosting(CreatePhotoVo vo, User currentUser, boolean resizing)  {

        List<PostingDto> list = new ArrayList<>();

        PhotoAlbum photoAlbum = photoAlbumService.getTheWholeAlbum(currentUser); //전체 앨범

        for(int i=0; i<vo.getFiles().length;i++){

            MultipartFile file = vo.getFiles()[i];

            Photo photo = new Photo();
            photo.setOwner(currentUser);
            if(vo.getFiles().length ==1 && (Objects.isNull(vo.getTexts()) || vo.getTexts().length==0)){
                photo.setText("");
            }
            else {
                photo.setText(vo.getTexts()[i]);
            }

            List<PostingAttachment> attachmentList = new ArrayList<>();
            if(file.isEmpty()) continue;

            ImageUploadService.ImageResourceInfoWithMetadata resourceWithMeta = uploadImageFn.apply(file, resizing);
            PostingAttachment attachment = PostingAttachment.builder().parent(photo)
                    .imageResource(resourceWithMeta.getImageResource())
                    .build();
            attachmentList.add(attachment);

            userRepository.updateIncrementPostingCount(currentUser.getId());
            photo.setAttachments(attachmentList);

            postingRepository.save(photo);
            photoPhotoAlbumService.getPhotoPhotoAlbumByPhotoAndPhotoAlbum(photo, photoAlbum).ifPresent(photoPhotoAlbum -> {throw new FlabookGlobalException("이미 존재합니다.");} );

            photoAlbumService.updateIncrementPhotoCnt(photoAlbum.getId());
            userRepository.updateIncrementPhotoPostingCount(currentUser.getId());

            PhotoPhotoAlbum photoPhotoAlbum = new PhotoPhotoAlbum(photo, photoAlbum);
            photoPhotoAlbumService.save(photoPhotoAlbum);

            photoRecentWriterRepository.save(new PhotoRecentWriter(new UserPostingRelation(currentUser, photo), ZonedDateTime.now()));
            list.add(PostingDto.of(photo));
        }

        List<Long> collect = list.stream().map(PostingDto::getId).collect(Collectors.toList());

        List<String> albumNames = new ArrayList<>(Arrays.asList(vo.getAlbumNames()));
        albumNames.removeIf(name -> name.equals("전체"));
        copyPhotoPosting( collect, albumNames, currentUser, false, null);

        return list;

    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValues.POSTINGS, key = "#id"),
            @CacheEvict(value = "photoAlbum", key = "#user.id")
    })
    public PostingDto updatePhotoPosting(Long id, UpdatePhotoVo vo, User user) {
        Posting posting = postingRepository.findById(id).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY + id));
        if(!AccessControl.isAllowed(posting.getOwner(), user) || posting.isDelete() || posting.isBlind()) {
            throw new FlabookPermissionException("권한이 없습니다.");
        }

        posting.setText(vo.getText());

        posting = postingRepository.save(posting);
        return PostingDto.of(posting);
    }

    @Transactional
    @CacheEvict(value = "photoAlbum", key = "#user.id")
    public boolean movePhotoPosting(List<Long> ids, String beforeAlbumName, String afterAlbumName, User user, boolean all) {

        PhotoAlbum beforePhotoAlbum = photoAlbumService.getPhotoAlbumByName(user, beforeAlbumName);
        PhotoAlbum afterPhotoAlbum = photoAlbumService.getPhotoAlbumByName(user, afterAlbumName);

        if(afterPhotoAlbum.getName().equals("전체") || beforePhotoAlbum.getName().equals("전체")) throw new FlabookGlobalException("이동이 불가한 위치 입니다.");

        if(all){
            ids = beforePhotoAlbum.getPhotos().stream().map(PhotoPhotoAlbum::getPhoto).map(Posting::getId).collect(Collectors.toList());
        }

        for(Long id : ids) {
            Posting posting = postingRepository.findById(id).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY + id));
            if (!AccessControl.isAllowed(posting.getOwner(), user) || posting.isDelete() || posting.isBlind() || !posting.getOwner().equals(user)) {
                throw new FlabookPermissionException("권한이 없습니다.");
            }

            Photo photo = (Photo) posting;

            if(photoPhotoAlbumService.getPhotoPhotoAlbumByPhotoAndPhotoAlbum(photo, afterPhotoAlbum).isPresent()){
                if(beforeAlbumName.equals(afterAlbumName))
                    continue;
                photoPhotoAlbumService.remove(photo, beforePhotoAlbum);
                postingRepository.save(photo);
                photoAlbumService.updateDecrementPhotoCnt(beforePhotoAlbum.getId());
                continue;
            }

            photoPhotoAlbumService.remove(photo, beforePhotoAlbum);
            photoPhotoAlbumService.save(new PhotoPhotoAlbum(photo, afterPhotoAlbum));
            postingRepository.save(photo);

            photoAlbumService.updateDecrementPhotoCnt(beforePhotoAlbum.getId());
            photoAlbumService.updateIncrementPhotoCnt(afterPhotoAlbum.getId());

        }
        return true;
    }

    @Transactional
    @CacheEvict(value = "photoAlbum", key = "#user.id")
    public List<PostingDto> deletePhotoPosting(List<Long> ids, User user, String albumName, boolean all) throws FlabookGlobalException {

        PhotoAlbum photoAlbum = photoAlbumService.getPhotoAlbumByName(user, albumName);

        if(all){
            ids = photoAlbum.getPhotos().stream().map(PhotoPhotoAlbum::getPhoto).map(Posting::getId).collect(Collectors.toList());
        }

        List<PostingDto> list = new ArrayList<>();
        for(Long id : ids) {
            Posting posting = findPostingLockMode(id, user);
            Photo photo = (Photo) posting;
            if(!photo.getOwner().equals(user) && !user.getRole().equals(UserRole.ADMIN)) throw new FlabookGlobalException("삭제 권한이 없습니다.");

            if(albumName.equals("전체")) {
                for( PhotoPhotoAlbum ppa : photo.getPhotoAlbums()){
                    photoAlbumService.updateDecrementPhotoCnt( ppa.getPhotoAlbum().getId() );
                }
                rankingService.removePostingRankingByPhoto(photo);
                photoPhotoAlbumService.removeAll(photo.getPhotoAlbums());
                photoRecentWriterService.checkAndRemovePosting(photo.getId());
                postingRepository.delete(photo);

                userRepository.updateDecrementPhotoPostingCount(posting.getOwner().getId());
                userRepository.updateDecrementPostingCount(photo.getOwner().getId());
                userRepository.updateDecrementPhotoLikeCount(photo.getOwner().getId(), photo.getLikeCount());
            }
            else{
                photoPhotoAlbumService.remove(photo, photoAlbum);
                photoAlbumService.updateDecrementPhotoCnt(photoAlbum.getId());
            }

            list.add(PostingDto.of(photo));


        }
        return list;
    }

    @Transactional
    @CacheEvict(value = "photoAlbum", key = "#user.id")
    public boolean copyPhotoPosting(List<Long> ids, List<String> afterAlbumNames, User user, boolean all, String currentAlbumName) {

        if(all){
            PhotoAlbum currentAlbum = photoAlbumService.getPhotoAlbumByName(user, currentAlbumName);
            ids = currentAlbum.getPhotos().stream().map(PhotoPhotoAlbum::getPhoto).map(Posting::getId).collect(Collectors.toList());
        }

        for (Long id : ids) {
            Posting posting = postingRepository.findById(id).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY + id));
            if (!AccessControl.isAllowed(posting.getOwner(), user) || posting.isDelete() || posting.isBlind() || !posting.getOwner().equals(user)) {
                throw new FlabookPermissionException("권한이 없습니다.");
            }

            Photo photo = (Photo) posting;

            for(String afterAlbumName : afterAlbumNames) {
                PhotoAlbum photoAlbum = photoAlbumService.getPhotoAlbumByName(user, afterAlbumName);
                if (afterAlbumName.equals("전체")) throw new FlabookGlobalException("전체로는 복사할 수 없습니다.");

                if( photoPhotoAlbumService.getPhotoPhotoAlbumByPhotoAndPhotoAlbum(photo, photoAlbum).isPresent() )
                    continue;

                PhotoPhotoAlbum photoPhotoAlbum = new PhotoPhotoAlbum(photo, photoAlbum);
                photoPhotoAlbumService.save(photoPhotoAlbum);
                postingRepository.save(photo);

                photoAlbumService.updateIncrementPhotoCnt(photoAlbum.getId());
            }

        }
        return true;

    }


    @Transactional
    public CommonResponse<PostingDto> doReadPosting(long id, User currentUser) {
        return findPosting(id).map(postingDto -> {
            boolean isLike = likePostingService.isLikePost(id, currentUser);
            postingDto.setIsLike(isLike);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingDto);
        }).orElseGet(() -> new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, "게시글을 찾을 수 없습니다."));
    }

    public CommonResponse<Boolean> doLikePosting(long id, User currentUser) {
        try {
            likePostingService.addLike(id, currentUser);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), true);
        } catch (EntityNotFoundException e) {
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, CommonMessages.NOT_FOUND_POSTING);
        }
    }

    public CommonResponse<Boolean> doUnLikePosting(long id, User currentUser) {
        try {
            likePostingService.removeLike(id, currentUser);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), false);
        } catch (EntityNotFoundException e) {
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, CommonMessages.NOT_FOUND_POSTING);
        }
    }

    public CommonResponse<Boolean> doReportPosting(long id, User currentUser, CreateReportVo vo) {
        try {
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), reportPostingService.addReport(id, currentUser.getId(), vo));
        } catch (EntityNotFoundException e) {
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, CommonMessages.NOT_FOUND_POSTING);
        }
    }


}
