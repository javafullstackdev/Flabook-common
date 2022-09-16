package com.msquare.flabook.push.model;


import com.msquare.flabook.dto.NotificationDto;

import java.util.List;

public interface PushNotification {

	/**
	 * PUSH 노출 제목
	 * @return  발송 제목
	 */
	String getPushTitle();

	/**
	 * PUSH 노출 컨텐츠
	 * @return  발송 컨텐츠
	 */
	String getPushContent();

	/**
	 * PUSH 발송 사용자 토큰
	 * @return  발송 사용자 토큰
	 */
	String getPushUserToken();

	/**
	 * PUSH 발송 사용자 토큰
	 * @return  발송 사용자 토큰
	 */
	List<String> getPushUserTokens();


	/**
	 * PUSH 발송 토픽
	 * @return  발송 토픽
	 */
	String getPushTopic();

	/**
	 * PUSH 발송 타겟 유니크 아이디
	 *
	 * @return  발송 타겟 유니크 아이디
	 */
	String getPushTargetId();

	/**
	 * PUSH 타겟 안드로이드 - 액티비티
	 *
	 * @return  타겟 안드로이드 - 액티비티
	 */
	String getPushTargetActivity();


	/**
	 * PUSH 뱃지 개수
	 *
	 * @return  뱃지 개수
	 */
	long getPushBadgeCount();

    NotificationDto getTarget();

    String getImageUrlPath();

    boolean getWithNotificationField();
}

