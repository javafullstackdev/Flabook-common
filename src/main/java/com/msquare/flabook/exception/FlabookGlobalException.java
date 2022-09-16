package com.msquare.flabook.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FlabookGlobalException extends RuntimeException {

    public static final class Messages {

        private Messages() {

        }

        public static final String NOT_FOUND_ENTITY = "not found entity : ";
        public static final String DELETED_POSTING =  "삭제된 게시물입니다.";
        public static final String DO_NOT_DELETE_COMMENT = "댓글이 달린 게시물은 삭제할 수 없습니다.";
        public static final String PLEASE_SELECT_PHOTO = "사진을 선택해주세요.";
        public static final String PLEASE_SELECT_POSTER = "poster 를 추가해주세요.";
        public static final String NOT_FOUND_POSTING = "게시글을 찾을 수 없습니다.";
        public static final String NOT_FOUND_RESOURCE = "리소스를 찾을 수 없습니다.";

        public static final String PLEASE_WAIT = "잠시 후 다시 시도해주세요.";
        public static final String CAN_NOT_SHARE_POSTING = "공유할 수 없는 게시글 입니다.";
        public static final String CAN_NOT_SHARE_EVENT = "공유할 수 없는 이벤트 입니다.";
    }

    public FlabookGlobalException(String message) {
        super(message);
    }
}
