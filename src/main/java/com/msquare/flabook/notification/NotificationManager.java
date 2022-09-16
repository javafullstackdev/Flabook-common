package com.msquare.flabook.notification;

import java.util.*;

import com.msquare.flabook.enumeration.OsType;
import com.msquare.flabook.models.Resource;
import com.msquare.flabook.service.PushNotificationServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.configurations.SubscribeTopics;
import com.msquare.flabook.dto.NotificationDto;
import com.msquare.flabook.enumeration.EventType;
import com.msquare.flabook.models.Notification;
import com.msquare.flabook.service.PushNotificationService;
import com.msquare.flabook.service.UserService;

@Slf4j
@Component
@AllArgsConstructor
public class NotificationManager {

    private final PushNotificationService pushNotificationService;
    private final UserService userService;

    @SuppressWarnings("unused")
    public void sendNotification(Notification notification) {
        this.sendNotification(notification, Optional.of(OsType.all));
    }

    public void sendNotification(Notification notification, Optional<OsType> optionalOsType) { //NOSONAR
        OsType osType = optionalOsType.orElse(OsType.all);
        NotificationDto notificationDto = NotificationDto.of(notification);

        List<UserService.NotiRecipientInfo> recipients = new ArrayList<>();

        Set<String> supportOsTypes = new HashSet<>();
        if(OsType.android.equals(osType)) {
            supportOsTypes.add(PushNotificationServiceImpl.SupportOsType.ANDROID);
        } else if(OsType.ios.equals(osType)) {
            supportOsTypes.add(PushNotificationServiceImpl.SupportOsType.IOS);
        } else {
            supportOsTypes.add(PushNotificationServiceImpl.SupportOsType.ANDROID);
            supportOsTypes.add(PushNotificationServiceImpl.SupportOsType.IOS);
            supportOsTypes.add(PushNotificationServiceImpl.SupportOsType.CHROME);
        }

        switch (notification.asResource().getResourceType()) {
            case notice:
                for(String supportOsType : supportOsTypes) {
                    recipients.add(new UserService.NotiRecipientInfo(0L, supportOsType, SubscribeTopics.getTopicByOsType(SubscribeTopics.DEFAULT_TOPIC, supportOsType), 1L, 0L, true));
                }

                break;

            case advertisement:
                for(String supportOsType : supportOsTypes) {
                    recipients.add(new UserService.NotiRecipientInfo(0L, supportOsType, SubscribeTopics.getTopicByOsType(SubscribeTopics.AD_TOPIC, supportOsType), 1L, 0L, true));
                }
                break;

            default:

                String topic = null;
            	if(notification.asResource().getResourceType().isBoardType()) {
            	    topic = SubscribeTopics.POSTING_TOPIC;

            	} else if(Resource.ResourceType.web.equals(notification.asResource().getReferenceType())) {
            	    topic = SubscribeTopics.DEFAULT_TOPIC;
                }

            	if(topic != null && EventType.ADMIN_CUSTOM_ALL.equals(notification.getEventType())) {
                    for(String supportOsType : supportOsTypes) {
                        recipients.add(new UserService.NotiRecipientInfo(0L, supportOsType, SubscribeTopics.getTopicByOsType(topic, supportOsType), 1L, 0L, true));
                    }
                }

                recipients.addAll(userService.findEnableFcmTokensByUserIds(notification.getRecipients().keySet()));
                break;
        }

        recipients.removeIf(recipient -> !supportOsTypes.contains(recipient.getOsType()));

        if(recipients.isEmpty()) {
            log.debug("recipients is empty");
        } else {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){
                @Override
                public void afterCommit(){
                    pushNotificationService.send(notificationDto, recipients);
                    log.debug("commit!!!");
                }
            });
        }
    }

}
