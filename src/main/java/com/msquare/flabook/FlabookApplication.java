package com.msquare.flabook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableFeignClients(basePackages = "com.msquare.flabook.*")
@ComponentScan(value = "com.msquare.flabook.*")
public class FlabookApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlabookApplication.class, args);
    }

}
