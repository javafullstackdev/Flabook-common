package com.msquare.flabook.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SnsUserProfile {

    private String id;
    private Properties properties;

    @SuppressWarnings("java:S116")
    @Getter
    @Setter
    @ToString
    public static class Properties {
        private String nickname;
        private String thumbnail_image;
        private String profile_image;
        private String account_email;
    }
}
