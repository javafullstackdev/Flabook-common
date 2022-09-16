package com.msquare.flabook.service.elasticsearch;

public class CacheValues {

    private CacheValues() throws IllegalAccessException{
        throw new IllegalAccessException("CacheValues is util");
    }

    public static final String HOME = "home";

    public static final String POSTINGS = "postings";

    /**
     * 언급된 사용자 리스트
    */
    public static final String MENTION_USERS = "mention_users";

    /**
     * 게시글 연관상품
     */
    public static final String RELATION_GOODSES = "relation_goodses";

    public static final String SIMILAR_CONTENT_LIST = "posting_similar_content";
    public static final String POSTING_TIMELINE = "posting_timeline";
    public static final String POSTING_SEARCH_COUNT = "posting_search_count";

    public static final String SHARES = "shares";

    public static final String GOODS_METATAG = "goods_metatag";
    public static final String USERS = "users";
    public static final String RECOMMEND_KEYWORDS = "recommend_keywords" ;
    public static final String COMMENT_LIST = "comment_list";

    public static final String TAGS = "tags";
    public static final String GOODS_SEARCH = "goods_search";
    public static final String GOODS_SEARCH_RESULT_COUNT = "goods_search_count";
    public static final String FIND_USER_BY_ACCESS_TOKEN = "user_select_by_access_token";
    public static final String RELEASE_NOTES = "release_notes";


    /**
     * 로그인 사용자 체크
     */
    public static final String LOGIN_USER = "login_user";

    public static final String RANKING = "ranking";

    public static final String PHOTO_WRITERS = "photo_writers";
}
