package com.msquare.flabook.aspect;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.api.service.UserLoginCheckService;
import com.msquare.flabook.models.User;
import com.msquare.flabook.queue.LoginMessage;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * 사용자 일일 접속 체크
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserDauAspect {

    @NonNull
    private final UserLoginCheckService loginCheckService;
    @NonNull
    private final StringRedisTemplate stringRedisTemplate;
    @NonNull
    private final AmqpTemplate template;

    private String lastDayKey = "";

    @Pointcut("execution(* com.msquare.flabook.controller.HomeController.doSearch*(..) )")
    public void homeControllerAdvice() {
        //
    }

    /**
     * home Controller api 호출시점에 로그인된 사용자로 본다.
     */
    @Before("homeControllerAdvice()")
    public void aroundUserAdvice() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null)
            return;

        Object principal = auth.getPrincipal();
        if(!(principal instanceof User))
            return;

        User user = (User) principal;
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Asia/Seoul"));
        ZonedDateTime lastLogin = loginCheckService.lastLogin(user.getId(), now);

        if(!lastLogin.equals(now))
            return;

        int dayOfYear = now.getDayOfYear();
        String key = String.format("login-%d", dayOfYear);
        Double result = stringRedisTemplate.opsForZSet()
                .incrementScore(key, String.valueOf(user.getId()), 1D);
        if(!lastDayKey.equals(key)) {
            lastDayKey = key;
            Date expireAt = Date.from(now.plusDays(1).toLocalDate().atTime(0, 0).toInstant(ZoneOffset.ofHours(9)));
            stringRedisTemplate.expireAt(key, expireAt);
            if(log.isDebugEnabled()) {
                log.debug("expire : {}", expireAt);
            }
        }

        //캐시가 초기값인 경우 로그인
        if(result != null && result.equals(1D)) {
            template.convertAndSend("loginQueue", new LoginMessage(user.getId(), ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).toInstant().getEpochSecond()));
        }

        if(log.isDebugEnabled()) {
            log.debug("userAspect {}, {}, {}", dayOfYear, user, result);
        }
    }


}
