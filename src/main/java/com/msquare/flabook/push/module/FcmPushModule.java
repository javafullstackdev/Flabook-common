package com.msquare.flabook.push.module;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.dto.NotificationDto;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.UserPushToken;
import com.msquare.flabook.push.model.PushNotification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class FcmPushModule {

    private final String pushServerKey;
    private static final String PUSH_SERVER_URL = "https://fcm.googleapis.com/fcm/send";

    private static final String IID_HOST = "https://iid.googleapis.com";
    private static final String IID_SUBSCRIBE_PATH = "iid/v1:batchAdd";
    private static final String IID_UNSUBSCRIBE_PATH = "iid/v1:batchRemove";

    private final ObjectWriter objectWriter;


    /**
     * FCM Module Constructor
     *
     * @param pushServerKey FCM PUSH 서버 키
     */
    public FcmPushModule(String pushServerKey, ObjectMapper objectMapper) {
        log.debug("ChatPushModule PUSH_SERVER_KEY : {}", pushServerKey);
        this.pushServerKey = pushServerKey;

        this.objectWriter = objectMapper//.disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .writerWithView(Views.BaseView.class);

    }

    @Data
    @JsonView(Views.BaseView.class)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private static class PushNofitificationModel {

        @Accessors(chain = true)
        @JsonProperty("registration_ids")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<String> registrationIds;

        @Accessors(chain = true)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String to;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Accessors(chain = true)
        private Notification notification;

        @Accessors(chain = true)
        private PushData data;

        /**
         * ios push image 처리용
         */
        @JsonProperty("mutable_content")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private boolean mutableContent;
    }

    @Data
    @JsonView(Views.BaseView.class)
    @AllArgsConstructor
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private static class Notification {

        public Notification(@NonNull String title, @NonNull String body, @NonNull String clickAction, long badge) {
            this.title = title;
            this.body = body;
            this.clickAction = clickAction;
            this.badge = badge;
        }

        @NonNull
        private String title;

        @NonNull
        private String body;

        @NonNull
        @JsonProperty("click_action")
        private String clickAction;

        private long badge;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("sound")
        private String sound = "default";

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("mutable-content")
        private Integer mutableContent;

        private String icon = "favicon.ico";
    }

    @Data
    @JsonView(Views.BaseView.class)
    @RequiredArgsConstructor
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private static class PushData {

        public PushData(@NonNull NotificationDto value, long targetBadge) {
            this.value = value;
            this.targetBadge = targetBadge;
        }

        @NonNull
        private NotificationDto value;
        private String targetId;

        private String imageUrlPath;

        /*
         * 채팅방별 메시지
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private long targetBadge;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String title;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String body;
    }

    private boolean manageTopicOp(String topic, List<UserPushToken> userPushTokens, String operation ) {
        try {

            final String prefixedTopic;
            if (topic.startsWith("/topics/")) {
                prefixedTopic = topic;
            } else {
                prefixedTopic = "/topics/" + topic;
            }

            // FCM or Google REST API URL
            URL url = new URL(String.format("%s/%s", IID_HOST, operation));

            // Connection Header Value Input.
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + pushServerKey);
            conn.setDoOutput(true);

            // The array of IID tokens for the app instances you want to add or remove.
            List<String> registrationTokens = userPushTokens.stream().map(UserPushToken::getToken).collect(Collectors.toList());

            // Input Json Value
            Map<String, Object> input = new HashMap<>();
            input.put("to", prefixedTopic);
            input.put("registration_tokens", registrationTokens);

            OutputStream os = conn.getOutputStream();
            os.write(objectWriter.writeValueAsString(input).getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            if(conn.getResponseCode() != 200) {
                return false;
            }

            if(log.isDebugEnabled()) {
                log.debug("Sending 'POST' request to URL : " + url);
                responseBodyLog(conn);
                log.debug("ResponseCode[{}], {} To Topic[{}], userToken[{}]", conn.getResponseCode(), operation, topic, registrationTokens);
            }

            conn.disconnect();

        } catch (Exception e) {
            log.error("이벤트 토픽 {} {} 실패 : {}", operation, topic, e);
            return false;
        }
        log.debug("이벤트 토픽 {} {} 성공", operation, topic);
        return true;
    }


    /**
     * @param topic 토픽
     * @param userPushTokens 100개 이내
     * @return boolean
     */
    public boolean subscribeTopic(String topic, List<UserPushToken> userPushTokens) {
        return manageTopicOp(topic, userPushTokens, IID_SUBSCRIBE_PATH);
    }

    /**
     * @param topic 토픽
     * @param userPushTokens 100개 이내
     * @return boolean
     */
    public boolean unsubscribeTopic(String topic, List<UserPushToken> userPushTokens) {
        return manageTopicOp(topic, userPushTokens, IID_UNSUBSCRIBE_PATH);
    }

    private void responseBodyLog(HttpURLConnection conn) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        log.debug("Response Parameter: {}", response);
    }


    private PushData createPushData(PushNofitificationModel pushNotificationModel, PushNotification pushNotification) {
        PushData pushData = new PushData(pushNotification.getTarget()/*, pushNotification.getPushTargetId()*/, pushNotification.getPushBadgeCount());

        String imageUrl = pushNotification.getImageUrlPath();
        if(pushNotification.getWithNotificationField() && imageUrl != null && !imageUrl.isEmpty()) {
            //ios 일경우 webp -> jpg 로 변환
            imageUrl += (imageUrl.contains("?")) ? "&f=jpeg" :  "?f=jpeg";
        }
        pushData.setImageUrlPath(imageUrl);

        if(pushNotification.getWithNotificationField()) {
            Notification notification = new Notification(pushNotification.getPushTitle(), pushNotification.getPushContent(),pushNotification.getPushTargetActivity(), pushNotification.getPushBadgeCount());
            pushNotificationModel.setNotification(notification);

            if(pushNotification.getImageUrlPath() != null && !pushNotification.getImageUrlPath().isEmpty()) {
                pushNotificationModel.setMutableContent(true);
            }

        } else {
            pushData.setTitle(pushNotification.getPushTitle());
            pushData.setBody(pushNotification.getPushContent());
        }

        return pushData;
    }

    public boolean sendPushMsg(PushNotification pushNotification){

        PushNofitificationModel pushNotificationModel = new PushNofitificationModel();

        String pushTopic = pushNotification.getPushTopic();
        // Topic 값이 존재 하는 경우
        boolean isTopicMessage = false;
        if(pushTopic != null && !pushTopic.isEmpty()) {
            // 토픽 발송
            isTopicMessage = true;
            pushNotificationModel.setTo(pushNotification.getPushTopic());
            // 없을 경우, 사용자 발송
        } else if(pushNotification.getPushUserTokens() != null && !pushNotification.getPushUserTokens().isEmpty()) {
            if(pushNotification.getPushUserTokens().size() == 1) {
                pushNotificationModel.setTo(pushNotification.getPushUserTokens().get(0));
            } else {
                pushNotificationModel.setRegistrationIds(pushNotification.getPushUserTokens());
            }
        } else if(pushNotification.getPushUserToken() != null && !pushNotification.getPushUserToken().isEmpty()) {
            pushNotificationModel.setTo(pushNotification.getPushUserToken());
        } else {
            log.debug("undefined token");
            return false;
        }

        PushData pushData = createPushData(pushNotificationModel, pushNotification);
        pushNotificationModel.setData(pushData);

        if(log.isDebugEnabled())
            log.debug("Send : {}, topic {}", pushData, isTopicMessage);

        try {

            String requestBody = objectWriter.writeValueAsString(pushNotificationModel);
            if(log.isTraceEnabled())
                log.trace("requestBody : {}", requestBody);
            URL url = new URL(PUSH_SERVER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + pushServerKey);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();

            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            if(conn.getResponseCode() != 200) {
                return false;
            }

            if(log.isDebugEnabled()) {
                log.debug("Sending 'POST' request to URL : " + url);
                log.debug("Post parameters : " + requestBody);
                log.debug("Response Code2 : " + conn.getResponseCode());
                responseBodyLog(conn);
            }
        } catch (IOException e) {
            log.error("ChatPushModule", e);
            return false;
        }
        return true;
    }
}
