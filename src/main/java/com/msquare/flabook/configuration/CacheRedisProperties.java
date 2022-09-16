package com.msquare.flabook.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.cache.redis")
public class CacheRedisProperties {

    private Duration defaultExpireTime = Duration.ofSeconds(10);

    private Map<String, Duration> expireTime = new HashMap<>();


    public Duration getDefaultExpireTime() {
        return defaultExpireTime;
    }

    public void setDefaultExpireTime(Duration defaultExpireTime) {
        this.defaultExpireTime = defaultExpireTime;
    }

    public Map<String, Duration> getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Map<String, Duration> expireTime) {
        this.expireTime = expireTime;
    }

}
