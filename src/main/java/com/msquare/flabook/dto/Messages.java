package com.msquare.flabook.dto;

class Messages {

    private Messages() throws IllegalAccessException {
        throw new IllegalAccessException("Messages is static");
    }

    @SuppressWarnings("unused")
    static final String BAN_USER_MESSAGE = "삭제된 글입니다.";
    @SuppressWarnings("unused")
    static final String DELETED_COMMENT_MESSAGE = "삭제된 답변입니다.";
    static final String DELETED_MESSAGE = "삭제된 글입니다.";

    static final String BLIND_MESSAGE = "신고에 의해 차단된 글입니다.";

}
