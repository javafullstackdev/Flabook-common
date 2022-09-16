package com.msquare.flabook.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.github.resilience4j.timelimiter.TimeLimiterConfig.custom;
import static java.time.Duration.ofMillis;

@Configuration
public class Resilience4JConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> customizer() {
        return factory -> factory
                .configure(
                        builder -> builder
                            .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                            // cancelRunningFuture 가 false 이면 로직이 future 로 동작하지 않는다.
                            .timeLimiterConfig(custom().cancelRunningFuture(false).timeoutDuration(ofMillis(5000)).build())
                            .build()
                        ,"postingCircuitBreaker"
                        , "commentCircuitBreaker"
                        , "searchCircuitBreaker"
                        , "tagCircuitBreaker"
                        , "userCircuitBreaker"
                        , "homeCircuitBreaker"
                        , "likeCircuitBreaker"
                        , "shareCircuitBreaker"
                        , "dictionaryCircuitBreaker"

                );
    }
}
