package com.msquare.flabook.common.configurations;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class ServiceHost {

    private final ServiceConfig beanConfig;
    private static ServiceConfig serviceConfig;

    public ServiceHost(ServiceConfig beanConfig) {
        this.beanConfig = beanConfig;
    }

    @PostConstruct
    private void initialize() {
        synchronized (ServiceHost.class) {
            if(serviceConfig == null) {
                serviceConfig = beanConfig;
            }
        }
    }

    public static String getUrl() {
        return serviceConfig.url;
    }

    public static String getS3Url() {
        return serviceConfig.s3Url;
    }

    public static String getLogoUrl() {
        return serviceConfig.logoUrl;
    }

    public static String getUrl(String postfix) {
        if(postfix == null || postfix.isEmpty())
            return "";

        return buildUrl(serviceConfig.url, postfix);
    }

    public static String getS3Url(String postfix) {
        if(postfix == null || postfix.isEmpty())
            return null;

        return buildUrl(serviceConfig.s3Url, postfix);
    }

    private static String buildUrl(String prefix, String postfix) {

        int lastIndex = prefix.length() - 1;
        if(prefix.lastIndexOf("/") == lastIndex) {
            prefix = prefix.substring(0, lastIndex);
        }

        if(postfix.indexOf("/") == 0) {
            postfix = postfix.substring(1);
        }

        return String.format("%s/%s", prefix, postfix);
    }

}
