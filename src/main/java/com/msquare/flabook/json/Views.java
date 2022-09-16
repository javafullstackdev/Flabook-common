package com.msquare.flabook.json;

public class Views {
    /**
     *
     */
    public interface BaseView {

    }

    /**
     *
     */
    public interface IgnoreJsonView {

    }

    public interface MyProfileDetailJsonView extends UserProfileDetailJsonView {

    }

    public interface UserProfileDetailJsonView extends BaseView {

    }

    /**
     * 공유수, 연관상품 등등 ..
     */
    public interface PostingActivityDetailJsonView extends BaseView {

    }


    /**
     * 사용자 게시글 활동 내용 추가(좋아요, 알림받기...)
     */
    public interface PostingUserActivityJsonView extends PostingLikeOnlyJsonView, PostingWatchOnlyJsonView, PostingScrapOnlyJsonView {

    }

    /**
     * 사용자 좋아요 필드 추가
     */
    public interface PostingLikeOnlyJsonView extends BaseView {

    }

    /**
     * 사용자 알림받기 필드 추가.
     */
    public interface PostingWatchOnlyJsonView extends BaseView {

    }

    /**
     * 사용자 스크랩 필드 추가.
     */
    public interface PostingScrapOnlyJsonView extends BaseView {

    }

    /**
     * Web Admin
     */
    public interface WebAdminJsonView extends MyProfileDetailJsonView {

    }

}
