package com.msquare.flabook.enumeration;

public enum NoticeType {
    notice("공지"), //NOSONAR
    event("이벤트"); //NOSONAR

    private final String name;
    NoticeType(String name) {
        this.name = name;
    }
}
