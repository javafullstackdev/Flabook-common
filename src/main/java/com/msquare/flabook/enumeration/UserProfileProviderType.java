package com.msquare.flabook.enumeration;

@SuppressWarnings("java:S115")
public enum UserProfileProviderType {
	email("이메일"), phone("전화번호"), kakao("카카오"), naver("네이버"), facebook("페이스북"), apple("애플");

	String displayName;
	UserProfileProviderType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
