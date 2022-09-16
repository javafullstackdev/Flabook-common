package com.msquare.flabook.common.controllers;

public enum CommonResponseCode {

    SUCCESS(1000, "성공."),

    PARAMETER_ERROR(2000, "파라미터 이상."),

    USER_LOGIN_FAIL(2100, "이메일 또는 비밀번호를 확인하세요."),
    USER_EMAIL_DUP(2101, "이메일 중복입니다."),
    USER_NOT_EXIST(2102, "아이디 또는 비밀번호를 확인하세요."),
    USER_NAME_DUP(2103, "닉네임 중복입니다."),
    USER_NAME_NEED_REGIST(2104, "닉네임을 등록해 주세요."),
    USER_NAME_ALREADY_REGIST(2105, "이미 닉네임을 등록 하였습니다."),
    USER_NAME_REGIST_FAIL(2106, "닉네임을 등록 실패."),
    USER_PASSWORD_MISS_MATCH(2107, "아이디 또는 비밀번호를 확인하세요."),
    USER_SNS_PROVIDER_NOT_SUPPORT(2108, "지원하지 않는 가입매체 입니다."),
    USER_LOGIN_AUTH_FAIL(2109, "APP 접근 권한이 없습니다."),
    USER_AUTHKEY_MISS_MATCH(2110, "사용자 인증번호를 확인하세요."),
    USER_AUTHKEY_ALREADY_MATCH(2111, "이메일 인증이 완료된 사용자 입니다."),
    USER_AUTHKEY_CONFIRM_NEED(2112, "이메일 인증이 완료되지 않은 사용자 입니다."),
    USER_AUTHKEY_ALREADY_SEND_MAIL(2113, "#RESEND_TIME#초 후에 다시 시도하세요."),
    USER_AUTHKEY_LATE_FAIL(2114, "인증번호 확인 시간 초과 되었습니다."),
    USER_EMAIL_LATE_FAILL(2115, "이메일 발송 실패 하였습니다."),
    USER_ALREADY_LEAVE(2116, "탈퇴한 사용자입니다."),
    USER_SNS_GET_PROFILE_FAIL(2117, "사용자 정보를 가져오는데 실패하였습니다."),
    USER_AUTHKEY_ALREADY_SEND_PHONE(2118, "#RESEND_TIME#초 후에 다시 시도하세요."),
    USER_NAME_LOGIN_FAIL(2119, "닉네임을 확인하세요."),
    USER_SNS_PROVIDER_CHANGE(2120, "#PROVIDER_DISPLAY_NAME# 로그인으로 전환된 계정입니다."),
    USER_PROVIDER_NOT_SUPPORT(2204, "지원하지 않는 가입매체 입니다."),
    USER_LOGIN_DUP(2205, "등록된 사용자입니다."),

    BUSINESS_LOGIC_ERROR(2300, "메시지 내용 참조해 주세요."),

    APP_RELEASE_DATA_FAIL(8000, "해당 OS에 대한 버전 정보가 존재하지 않습니다."),
    FAIL(9000, "실패."),
    FILE_UPLOAD_FAIL(9100, "파일 업로드 실패"),
    FAIL_AUTH_HISTORY_NOT_FOUND(9101, "인증번호 발송 내역이 확인되지 않습니다."),

    TOKEN_AUTH_FAIL(9200, "토큰 인증 실패"),
    TOKEN_EMPTY(9201, "토큰을 넣어 주세요."),
    TOKEN_INVALID(9202, "유효한 토큰이 아닙니다."),
    TOKEN_EXPIRE(9203, "만료된 토큰입니다."),
    REFRESHTOKEN_EXPIRE(9204, "만료된 토큰입니다."),

    UNKNOWN_RESPONSE_TYPE(9900, "알수 없는 응답 포맷입니다. API 서버 담당자에게 확인 바랍니다.");

    private final int resultCode;
    private final String resultMessage;

    CommonResponseCode(int resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    public int getResultCode() {
        return this.resultCode;
    }

    public String getResultMessage() {
        return this.resultMessage;
    }
}
