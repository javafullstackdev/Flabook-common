package com.msquare.flabook.enumeration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum UserStatus {
    @JsonProperty("normal") NORMAL("정상")
    , @JsonProperty("ban") BAN("차단")
    , @JsonProperty("leave") LEAVE("탈퇴");

    private String name;

    UserStatus(String name) {
        this.name = name;
    }
}
