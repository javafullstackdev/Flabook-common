package com.msquare.flabook.aspect;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.models.User;
import com.msquare.flabook.queue.SearchQueryMessage;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * SearchQueryInterceptor 가 추가된 controller method 에 q (검색키워드) 필드를 추출해 message queue로 전송
 * mq로 보낸 데이터는 저장 + 통계 데이터로 쓴다.
 */
@Slf4j
@Aspect
@Component
@Order(0)
@RequiredArgsConstructor
public class SearchQueryAspect {

    @NonNull
    private final AmqpTemplate template;

    @Pointcut("@annotation(com.msquare.flabook.aspect.SearchQueryInterceptor)")
    public void searchQueryInterceptor() {
        //
    }

    @AfterReturning("searchQueryInterceptor()")
    public void searchQuery(JoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SearchQueryInterceptor interceptor = method.getAnnotation(SearchQueryInterceptor.class);
        for (int i = 0; i < method.getParameters().length; i++) {
            String paramName = method.getParameters()[i].getName();

            if(!interceptor.value().equals(paramName)) {
                continue;
            }

            User user = (User)Stream.of(joinPoint.getArgs()).filter(o -> o instanceof User).findFirst().orElse(null);
            Object[] parameterValues = joinPoint.getArgs();
            String query = (String)parameterValues[i];
            log.info("query interceptor user : {}, query : {}", (user != null) ? user.getId() : null, query);
            if(user != null) {
                template.convertAndSend("searchQueryQueue", new SearchQueryMessage(query, user.getId()));
            }
        }
    }
}
