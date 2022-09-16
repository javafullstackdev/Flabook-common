package com.msquare.flabook.enumeration;

public enum BannerStatus {
    open("공개"), //NOSONAR
    preview("대기"), //NOSONAR
    close("숨김"); //NOSONAR

    private final String name;
    BannerStatus(String name) {
        this.name = name;
    }
}
