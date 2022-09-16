package com.msquare.flabook.common.configurations;

import org.springframework.context.ApplicationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubscribeTopics {

    private SubscribeTopics() throws IllegalAccessException{
        throw new IllegalAccessException("SubscribeTopics is static");
    }

    /**
     * 공지, 앱알림
     */
    public static final String DEFAULT_TOPIC = "/topics/default";


    /**
     * 추천게시글알림
     */
    public static final String POSTING_TOPIC = "/topics/posting";


    /**
     * 광고
     */
    public static final String AD_TOPIC = "/topics/ad";

    public static String getTopicByOsType(String topic, String osType) {
    	ApplicationContext context = ApplicationContextProvider.getApplicationContext();
    	String springProfiles = context.getEnvironment().getProperty("spring.profiles.active");
    	String topicWithOsType = topic + "." + osType;

    	if(!"product".equals(springProfiles)) {
    		topicWithOsType += "."+springProfiles;
    		log.info("["+springProfiles+"] subscribeTopic : " + topicWithOsType);
    	}

        return topicWithOsType;
    }
}
