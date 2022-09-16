package com.msquare.flabook.util;

public class RequestParamValidator {

    private RequestParamValidator() throws IllegalAccessException {
        throw new IllegalAccessException("RequestParamValidator is static");
    }

    public static void validateOrderBy(String orderBy) throws IllegalArgumentException {
        switch (orderBy) {
            case "id":
            case "popular":
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 정렬기준");
        }
    }
}
