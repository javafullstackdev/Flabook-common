package com.msquare.flabook.api.service;

import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.service.elasticsearch.CacheValues;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


import java.time.ZonedDateTime;

@Slf4j
@Service
public class UserLoginCheckService {

    /**
     * 사용자가 이미 접속한 경우 이전 now 값을 return
     *
     * @param user
     * @return
     * @throws
     */
    @Cacheable(cacheResolver = "localCacheResolver", value = CacheValues.LOGIN_USER, key = "{#userId}", condition = "#userId != null")
    public ZonedDateTime lastLogin(Long  userId, ZonedDateTime now) {
        return now;
    }
}
