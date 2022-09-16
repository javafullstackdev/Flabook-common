package com.msquare.flabook.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

@RestController
public class CorsController {
    protected void withCors(ServletResponse res) {
        HttpServletResponse response = (HttpServletResponse) res;

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, Authorization");
    }

    @RequestMapping(method = RequestMethod.OPTIONS, path = "")
    public String doOptionsMain() {
        return "";
    }

    @RequestMapping(method = RequestMethod.OPTIONS, path = "/**")
    public String doOptions() {
        return "";
    }
}
