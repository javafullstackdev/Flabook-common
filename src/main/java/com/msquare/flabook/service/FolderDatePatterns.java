package com.msquare.flabook.service;

public class FolderDatePatterns {

    private FolderDatePatterns() throws IllegalAccessException{
        throw new IllegalAccessException("FolderDatePatterns is util");
    }

    public static final String NOTIFICATIONS = "'notifications'/yyyy/MM/dd/";
    public static final String COMMENTS = "'comments'/yyyy/MM/dd/";
    public static final String POSTINGS = "'postings'/yyyy/MM/dd/";
    public static final String NOTICES = "'notices'/yyyy/MM/dd/";
    public static final String USERS = "'users'/yyyy/MM/dd/";
    public static final String BANNERS = "'banners'/yyyy/MM/dd/";
    public static final String EVENTS = "'events'/yyyy/MM/dd/";
    public static final String BADGES = "'badges'/yyyy/MM/dd/";
}
