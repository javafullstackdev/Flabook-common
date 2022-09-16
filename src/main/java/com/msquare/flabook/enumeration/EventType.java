package com.msquare.flabook.enumeration;

import lombok.Getter;

@Getter
public enum EventType {

    NEW_QUESTION,
    NEW_BOAST,
    NEW_CLINIC,
    NEW_GUIDE,
    NEW_MAGAZINE,
    NEW_COMMENT,
    NEW_REPLY,
    NEW_MENTION,
    NEW_LIKE_POSTING,
    NEW_LIKE_COMMENT,
    NEW_AD(true), // 광고
    NEW_SHOP, //  쇼핑관련 (배송)

    ADMIN_CUSTOM_ALL(true), // 관리자 발송
    ADMIN_CUSTOM_EXPERT(true), // 관리자 발송
    ADMIN_CUSTOM_TEST(true), // 관리자 발송

    NEW_BADGE
    ;

    private boolean isTopic;

    EventType(boolean isTopic) {
        this.isTopic = isTopic;
    }

    EventType() {
        this.isTopic = false;
    }

}
