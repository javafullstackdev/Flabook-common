package com.msquare.flabook.enumeration;

@SuppressWarnings("java:S115")
public enum MailType {
    join, //가입, 이메일 인증
    resetPassword, //패스워드 초기화
    modifyProfile, //프로필, 이메일 등록
    modifyProvider //기존 휴대폰 로그인 사용자, provider 변경
}
