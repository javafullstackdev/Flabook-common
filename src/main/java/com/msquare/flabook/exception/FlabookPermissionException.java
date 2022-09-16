package com.msquare.flabook.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FlabookPermissionException extends FlabookGlobalException {

    public static final class Messages {

        private Messages() throws IllegalAccessException {
            throw new IllegalAccessException("Messages is static");
        }


        public static final String NOT_AUTHORIZED = "권한이 없습니다.";
    }

    public FlabookPermissionException(String message) {
        super(message);
    }
}
