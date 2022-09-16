package com.msquare.flabook.common.configurations;

import com.msquare.flabook.util.SendAuthMtMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

/**
 * 서버 의존성있는것 분리
 */
@Configuration
public class BeanInitializeConfig {
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SendAuthMtMessage getSendAuthMtMessage() {
        return new SendAuthMtMessage();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
