package com.msquare.flabook.common.controllers;

public class CommonMessages {

    private CommonMessages() throws IllegalAccessException {
        throw new IllegalAccessException("CommonMessages is static");
    }

    public static final String NOT_FOUND_POSTING = "게시물을 찾을 수 없습니다.";
}
