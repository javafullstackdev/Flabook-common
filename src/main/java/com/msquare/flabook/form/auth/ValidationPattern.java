package com.msquare.flabook.form.auth;

public class ValidationPattern {

	private ValidationPattern() throws IllegalAccessException {
		throw new IllegalAccessException("ValidationPattern is static");
	}

	public static final String PATTERN_NICKNAME_MESSAGE = "닉네임은 띄어쓰기 없이 한글, 영어, 숫자 등으로만 설정해주세요 (15자 이내)";
	public static final String PATTERN_NICKNAME_REGEXP = "[가-힣ㄱ-ㅎㅏ-ㅣA-Za-z0-9_]{1,15}";

	public static final String PATTERN_PASSWORD_MESSAGE = "비밀번호는 영문과 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 16자의 비밀번호여야 합니다.";
	public static final String PATTERN_PASSWORD_REGEXP = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}";

	public static final String PATTERN_DATE_MESSAGE = "날짜, 시간 형식은 yyyy-MM-dd HH:mm:ss 입니다.";
	public static final String PATTERN_DATE_REGEXP = "[0-9]{4}-[0-9]{2}-[0-9]{2}\\s[0-9]{2}:[0-9]{2}:[0-9]{2}";
}
