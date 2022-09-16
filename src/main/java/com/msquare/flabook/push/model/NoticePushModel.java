package com.msquare.flabook.push.model;

import lombok.Data;
import com.msquare.flabook.dto.NotificationDto;

import java.util.List;

@Data
public class NoticePushModel implements PushNotification {

	private String title;
	private String content;
	private String userToken;
	private String pushTopic;
	private List<String> userTokens;
	private NotificationDto target;


	private String targetId;
	private String targetActivity;
	private long badgeCount;
	private String imageUrlPath;
	private boolean withNotificationField = true;

	public NoticePushModel(){
		//
	}

	@Override
	public String getPushTitle() {
		return title;
	}

	@Override
	public String getPushContent() {
		return content;
	}

	@Override
	public String getPushUserToken() {
		return userToken;
	}

	@Override
	public String getPushTopic() {
		return pushTopic;
	}

	@Override
	public String getPushTargetId() {
		return targetId;
	}

	@Override
	public long getPushBadgeCount() {
		return badgeCount;
	}

	@Override
	public String getPushTargetActivity() {
		return targetActivity;
	}

	@Override
	public List<String> getPushUserTokens() {
		return userTokens;
	}

	@Override
	public NotificationDto getTarget() {return this.target;}

	@Override
	public boolean getWithNotificationField() {
		return withNotificationField;
	}
}
