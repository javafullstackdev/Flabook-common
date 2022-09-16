package com.msquare.flabook.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.models.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserAspect {

    @Pointcut("execution(* com.msquare.flabook.controller.*.*(..) )")
    public void restApiControllerAdvice() {
        //
    }

    @Around("restApiControllerAdvice()")
    public Object aroundUserAdvice(ProceedingJoinPoint pjp) throws Throwable {

        Object[] args = Arrays.stream(pjp.getArgs()).map(data -> {
            if (data instanceof User) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                data = auth.getPrincipal();
            }
            return data;
        }).toArray();

        return pjp.proceed(args);
    }

}
