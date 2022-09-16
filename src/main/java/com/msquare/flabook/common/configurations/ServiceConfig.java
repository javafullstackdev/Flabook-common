package com.msquare.flabook.common.configurations;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServiceConfig {

    @Value("${server.host:http://flabook-develop-rest-1302610380.ap-northeast-2.elb.amazonaws.com}")
    public String url;

    @Value("${server.s3host:https://flabook-develop.s3.ap-northeast-2.amazonaws.com/}")
    public String s3Url;

    @Value("${server.logo:https://flabook-develop.s3.ap-northeast-2.amazonaws.com/commons/logo.png}")
    public String logoUrl;

}
