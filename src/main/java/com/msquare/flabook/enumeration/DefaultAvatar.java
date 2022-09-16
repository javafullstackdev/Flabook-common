package com.msquare.flabook.enumeration;

import lombok.Getter;

@Getter
public enum DefaultAvatar {

    Avartar1("commons/profiles/profile-01.png"), //NOSONAR
    Avartar2("commons/profiles/profile-02.png"), //NOSONAR
    Avartar3("commons/profiles/profile-03.png"), //NOSONAR
    Avartar4("commons/profiles/profile-04.png"), //NOSONAR
    Avartar5("commons/profiles/profile-05.png"), //NOSONAR
    Avartar6("commons/profiles/profile-06.png"); //NOSONAR

    private final String path;

    DefaultAvatar(String path) {
        this.path = path;
    }

    public static DefaultAvatar findById(long id) {
        return DefaultAvatar.values()[(int)(id % DefaultAvatar.values().length)];
    }

}
