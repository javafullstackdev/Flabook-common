package com.msquare.flabook.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.models.User;

@Slf4j
public class AccessLogger {

    private AccessLogger() throws IllegalAccessException{
        throw new IllegalAccessException("AccessLogger is util");
    }

    private static final ObjectMapper objectWriter = new ObjectMapper().disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

    private static Long getStartTime(HttpServletRequest request) {
        Object obj = request.getAttribute("startTime");
        if (obj != null)
            return (Long) request.getAttribute("startTime");
        else
            return null;
    }

    public static String getUserId(Authentication auth) {
        if(auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            return user.getId().toString();
        }
        return null;
    }

    private static void values(HttpServletRequest request, Enumeration<String> element, Map<String, String> headers) {
        while (element.hasMoreElements()) {
            String headerName = element.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
    }

    public static void log(HttpServletRequest request, HttpServletResponse response) {
		/*유저 식별*/
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	String userId = getUserId(auth);
    	/*유저 식별 END*/

        Long startTime = getStartTime(request);
        long executionTime = (startTime != null) ? System.currentTimeMillis() - startTime : 0L;

        Map<String, Object> accessLogMap = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> requestInfos = new HashMap<>();
        Map<String, String> resultParams = new HashMap<>();
        Map<String, Object> responseInfos = new HashMap<>();

        values(request, request.getHeaderNames(), headers);
        values(request, request.getParameterNames(), requestInfos);

		/*resultParams*/
        String resultMsg = Optional.ofNullable(request.getAttribute("resultMsg")).map(Object::toString).orElse("");
        String resultCode = Optional.ofNullable(request.getAttribute("resultCode")).map(Object::toString).orElse("");

        resultParams.put("resultMsg", resultMsg);
        resultParams.put("resultCode", resultCode);
        /*resultParams END*/

		/*responseInfos*/
        responseInfos.put("method", request.getMethod());
        responseInfos.put("client", request.getServerName());
        responseInfos.put("host", request.getRemoteHost());

        String requestWithQueryString = (request.getQueryString() == null) ? request.getRequestURI() : request.getRequestURI() + "?" + request.getQueryString();
        responseInfos.put("code", response.getStatus());
        responseInfos.put("time", executionTime);
        responseInfos.put("uri", requestWithQueryString);
        /*responseInfos END*/

        if(userId != null) {
        	requestInfos.put("userId", userId);
        }

        accessLogMap.put("headers", headers);
        accessLogMap.put("resultParams", resultParams);
        accessLogMap.put("requestInfos", requestInfos);
        accessLogMap.put("responseInfos", responseInfos);

        try {
            log.info(objectWriter.writeValueAsString(accessLogMap));
        } catch (JsonProcessingException ex) {
            log.error("AccessLogger.log", ex);
        }
    }
}
