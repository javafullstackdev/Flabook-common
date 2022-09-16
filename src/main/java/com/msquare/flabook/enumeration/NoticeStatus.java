package com.msquare.flabook.enumeration;

@SuppressWarnings("java:S115")
public enum NoticeStatus {
    open("공개"),
    preview("대기"),
    close("숨김");

    private final String name;
    NoticeStatus(String name) {
        this.name = name;
    }
}
