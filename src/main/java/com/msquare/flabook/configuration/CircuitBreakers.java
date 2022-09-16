package com.msquare.flabook.configuration;

import lombok.AllArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CircuitBreakers {

    private final CircuitBreakerFactory<?, ?> cbf;

    @Bean("postingCircuitBreaker")
    public CircuitBreaker postingCircuitBreaker() {
        return this.cbf.create("postingCircuitBreaker");
    }

    @Bean("userCircuitBreaker")
    public CircuitBreaker userCircuitBreaker() {
        return this.cbf.create("userCircuitBreaker");
    }

    @Bean("homeCircuitBreaker")
    public CircuitBreaker homeCircuitBreaker() {
        return this.cbf.create("homeCircuitBreaker");
    }

    @Bean("likeCircuitBreaker")
    public CircuitBreaker likeCircuitBreaker() {
        return this.cbf.create("likeCircuitBreaker");
    }

    @Primary
    @Bean("searchCircuitBreaker")
    public CircuitBreaker searchCircuitBreaker() {
        return this.cbf.create("searchCircuitBreaker");
    }
}
