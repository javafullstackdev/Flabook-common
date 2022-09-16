package com.msquare.flabook.common.configurations;

public class ConfigKeys {

    private ConfigKeys() throws IllegalAccessException {
        throw new IllegalAccessException("ConfigKeys is static");
    }


    //actuator 접근 allow host 예) "{ip}, {ip}"
    @SuppressWarnings("unused")
    public static final String ACTUATOR_ALLOW_IPS = "spring.actuator.allowIps";

    //aws s3 route53 domain
    @SuppressWarnings("unused")
    public static final String AWS_CDN_PATH = "spring.cloud.aws.cdn.path";

    //jwt 토큰 키
    @SuppressWarnings("unused")
    public static final String AWS_LAMBDA_JWT_KEY = "spring.cloud.aws.lambda.jwt.key";

    //jwt 토큰 expiretime 설정 (초)
    @SuppressWarnings("unused")
    public static final String AWS_LAMBDA_JWT_EXPIRE_SECOND = "spring.cloud.aws.lambda.jwt.expireSecond";

    //redisson 설정 경로
    @SuppressWarnings("unused")
    public static final String REDISSON_CONFIG_PATH = "spring.jpa.properties.hibernate.cache.redisson.config";

    //fcm 전송 request threadpool size (default : 10)
    public static final String FCM_REQUEST_THREADPOOL = "spring.fcm.threadPool";

    //fcm 서버키
    public static final String FCM_REQUEST_SERVERKEY = "spring.fcm.serverKey";

    //Broker 상태 Jandi 로 전송 여부 (boolean)
    @SuppressWarnings("unused")
    public static final String JANDI_ENABLE = "spring.jandi.enable";

    //Broker 상태 Jandi 로 전송 Webhook URL
    @SuppressWarnings("unused")
    public static final String JANDI_WEBHOOK_URL = "spring.jandi.webhookUrl";

}
