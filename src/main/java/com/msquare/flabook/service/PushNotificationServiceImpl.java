package com.msquare.flabook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.configurations.ConfigKeys;
import com.msquare.flabook.common.configurations.SubscribeTopics;
import com.msquare.flabook.dto.NotificationDto;
import com.msquare.flabook.dto.ResourceDto;
import com.msquare.flabook.enumeration.EventType;
import com.msquare.flabook.models.UserPushToken;
import com.msquare.flabook.push.model.NoticePushModel;
import com.msquare.flabook.push.model.PushNotification;
import com.msquare.flabook.push.module.FcmPushModule;
import com.msquare.flabook.util.OsTypeUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service("PushNotificationService")
public class PushNotificationServiceImpl implements PushNotificationService {

    private final ExecutorService executorService;
    private final FcmPushModule pushModule;

    public PushNotificationServiceImpl(Environment env, ObjectMapper objectMapper) {

        Integer threadPool = env.getProperty(ConfigKeys.FCM_REQUEST_THREADPOOL, Integer.class, 10);
        String pushServerKey = env.getProperty(ConfigKeys.FCM_REQUEST_SERVERKEY, "AAAAXP3ZQHs:APA91bHV5FydXKWsakiQaRZGFVv0aQ0ByppHqQEV4qXGSyolArkaysmoGZ_lsB_teDb_l8dHJAUelSxirQCRLG7-URuTPtY8VjxUZIDsdkTOXnroY9EkksXgaPQ471zo_4qWuzZGwiuz");

        this.executorService = Executors.newFixedThreadPool(threadPool);
        this.pushModule = new FcmPushModule(pushServerKey, objectMapper);

        log.debug("PushNotificationServiceImpl threadPool : {}, serverKey : {}", threadPool, pushServerKey);
    }

    private static final int MAX_BODY_LENGTH = 40;
    private static final String APP_MAIN_ACTIVITY = ".MainActivity";

    private String subString(String body) {
        if(body != null && body.length() > MAX_BODY_LENGTH) {
            return body.substring(0, MAX_BODY_LENGTH) + "...";
        }

        return body;
    }

    public static class SupportOsType {

        private SupportOsType() throws IllegalAccessException {
            throw new IllegalAccessException("SupportOsType is static");
        }

        public static final String ANDROID = "android";
        public static final String IOS = "ios";
        public static final String CHROME = "chrome";
    }

    /**
     * topic + osType 으로 subscribe
     * @param topic 토픽
     * @param userPushTokens 토큰 목록
     * @return 구독성공여부
     */
    public boolean subscribeTopic(String topic, List<UserPushToken> userPushTokens) {
        boolean res = true;
        Map<String, List<UserPushToken>> osTypeMap = userPushTokens.stream().collect(Collectors.groupingBy(UserPushToken::getOsType, Collectors.toList()));
        for(Map.Entry<String, List<UserPushToken>> entry : osTypeMap.entrySet()) {
            String osType = entry.getKey();
            res = pushModule.subscribeTopic(SubscribeTopics.getTopicByOsType(topic, osType), userPushTokens) && res;
        }
        return res;
    }

    /**
     * topic + osType 으로 unsubscribe
     * @param topic 토픽
     * @param userPushTokens 토큰목록
     * @return 구독해지여부
     */
    public boolean unsubscribeTopic(String topic, List<UserPushToken> userPushTokens) {
        boolean res = true;
        Map<String, List<UserPushToken>> osTypeMap = userPushTokens.stream().collect(Collectors.groupingBy(UserPushToken::getOsType, Collectors.toList()));
        for(Map.Entry<String, List<UserPushToken>> entry : osTypeMap.entrySet()) {
            String osType = entry.getKey();
            res = pushModule.unsubscribeTopic(SubscribeTopics.getTopicByOsType(topic, osType), userPushTokens) && res;
        }
        return res;
    }

    private String getTargetActivityByOsType(String osType, NotificationDto message) {
        if(SupportOsType.ANDROID.equalsIgnoreCase(osType)) {
            return APP_MAIN_ACTIVITY;

        } else if(SupportOsType.IOS.equalsIgnoreCase(osType)) {
            return APP_MAIN_ACTIVITY;

        } else if(SupportOsType.CHROME.equalsIgnoreCase(osType)) {

            ResourceDto resource = message.getResource();
            switch (resource.getReferenceType()) {
                case question:
                    return "#question";
                case boast:
                    return "#boast";
                case free:
                    return "#free";
                case magazine:
                    return "#magazine";
                case clinic:
                    return "#clinic";
                case guidebook:
                    return "/flabookplus/1.html#click";
                case reply:
                    return "/flabookplus/2.html#click";
                case shop:
                    if (message.getEventType() == EventType.NEW_SHOP) {
                        return "#shop";
                    }
                    return "#shop";

                default:
                    return "#default";
            }


        } else {
            return APP_MAIN_ACTIVITY;
        }
    }

    /**
     * 여기서 처리하는 토픽은 osType 포함
     * @param message 발송메시지
     * @param pushInfos 수신자 목록
     */
    @Override
    public void send(NotificationDto message, List<UserService.NotiRecipientInfo> pushInfos) {

        Map<String, Map<Long, List<UserService.NotiRecipientInfo>>> badgeWithTokens = pushInfos.stream().collect(Collectors.groupingBy(UserService.NotiRecipientInfo::getOsType,  Collectors.groupingBy(UserService.NotiRecipientInfo::getBadge, Collectors.toList())));
        for(Map.Entry<String, Map<Long, List<UserService.NotiRecipientInfo>>> osTypeEntry : badgeWithTokens.entrySet()) {
            try {
                boolean withNotificationField = OsTypeUtils.withNotification(osTypeEntry.getKey());
                for (Map.Entry<Long, List<UserService.NotiRecipientInfo>> entry : osTypeEntry.getValue().entrySet()) {
                    String targetActivity = getTargetActivityByOsType(osTypeEntry.getKey(), message);
                    log.debug("pushNotificationService os : {}, targetActivity : {}", osTypeEntry.getKey(), targetActivity);
                    NoticePushModel noticePushModel = new NoticePushModel();
                    // PUSH 발송 데이터 생성

                    noticePushModel.setTitle(message.getTitle());

                    noticePushModel.setContent(subString(message.getDescription()));

                    noticePushModel.setTargetActivity(targetActivity);
                    noticePushModel.setImageUrlPath(message.getPhotoUrl());
                    noticePushModel.setTarget(message);

                    if(!entry.getValue().isEmpty() && entry.getValue().get(0).isTopic()) {
                        noticePushModel.setPushTopic(entry.getValue().get(0).getToken());
                    } else {
                        noticePushModel.setUserTokens(entry.getValue().stream().map(UserService.NotiRecipientInfo::getToken).collect(Collectors.toList()));
                    }

                    noticePushModel.setBadgeCount(entry.getKey());
                    //ios 는 notification field 포함
                    noticePushModel.setWithNotificationField(withNotificationField);

                    log.debug("pushNotificationService.preSend");
                    this.sendRequest(noticePushModel);
                }
            } catch (Exception e) {
                log.error("pushNotificationService.send", e);
            }
        }
    }

    /**
     * pushNotificationService executor 를 통해 실행
     * @param runnable runnable
     */
    private void submit(Runnable runnable) {
        executorService.submit(runnable);
    }

    private void sendRequest(PushNotification pushNotification) {
        log.debug("pushNotificationService.sendRequest");
        submit(() -> {
            try {
                boolean ok = pushModule.sendPushMsg(pushNotification);
                log.debug("pushModule.sendPushMsg : {}", ok);
            } catch (Exception e) {
                log.debug("pushModule.sendPushMsg error ", e);
            }
        });
    }

}
