package com.msquare.flabook.common.controllers;

public class AuthorizationMessages {

    private AuthorizationMessages() {}

	/*핸드폰 번호 인증 유효시간*/
    public static final int AUTH_ID_KEY_SEND_AT_PLUS_MINUTES = 1;
    public static final int AUTH_ID_KEY_CONFIRM_AT_PLUS_MINUTES = 30;

	/*이메일 인증 유효시간*/
    public static final int AUTH_MAIL_SEND_AT_PLUS_MINUTES = 1;
    public static final int AUTH_MAIL_CONFIRM_AT_PLUS_MINUTES = 30;

    /*비밀번호 변경 유효시간*/
    @SuppressWarnings("unused")
    public static final int PASSWORD_MODIFY_MAIL_SEND_AT_PLUS_MINUTES = 1;
    public static final int PASSWORD_MODIFY_MAIL_CONFIRM_AT_PLUS_MINUTES = 720;
}
