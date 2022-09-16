package com.msquare.flabook.service;

import com.msquare.flabook.dto.NotificationDto;
import com.msquare.flabook.models.UserPushToken;

import java.util.List;

public interface PushNotificationService {
    boolean subscribeTopic(String topic, List<UserPushToken> userPushTokens);
    boolean unsubscribeTopic(String topic, List<UserPushToken> userPushTokens);
    void send(NotificationDto message, List<UserService.NotiRecipientInfo> pushInfos);
}
