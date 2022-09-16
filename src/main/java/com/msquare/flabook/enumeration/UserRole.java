package com.msquare.flabook.enumeration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum UserRole {

    @JsonProperty("admin") ADMIN("관리자")
    , @JsonProperty("expert") EXPERT("전문가")
    , @JsonProperty("user") USER("사용자");

    private final String name;

    UserRole(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    public static boolean hasGlobalDeletePermission(UserRole userRole) {
        return ADMIN.equals(userRole);
    }
}
