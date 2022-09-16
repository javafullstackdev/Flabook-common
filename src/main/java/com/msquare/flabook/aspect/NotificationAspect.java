package com.msquare.flabook.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.service.NotificationService;
import com.msquare.flabook.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class NotificationAspect {

    private final NotificationService notificationService;

    @Pointcut("execution(* com.msquare.flabook.service.UserService.findEnableFcmTokensByUserIds(..) )")
    public void advice() {
        //
    }

    @Value("${flabook.notification.limit:100}")
    private int limit;

    @SuppressWarnings("unchecked")
    @Around("advice()")
    public Object aroundUserAdvice(ProceedingJoinPoint pjp) throws Throwable {
        List<UserService.NotiRecipientInfo> recipientInfos =  (List<UserService.NotiRecipientInfo>)pjp.proceed();
        log.info("recipientInfos : {}", recipientInfos);
        for(UserService.NotiRecipientInfo recipientInfo : recipientInfos) {
            if(recipientInfo.getCount() > limit) {
                notificationService.deleteByLimit(recipientInfo.getUserId(), limit);
            }
        }
        return recipientInfos;
    }

}
