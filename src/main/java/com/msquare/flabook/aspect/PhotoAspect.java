package com.msquare.flabook.aspect;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.PostingDto;
import com.msquare.flabook.models.User;
import com.msquare.flabook.queue.ReadMessage;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PhotoAspect {

    @NonNull
    private final AmqpTemplate template;

    @SuppressWarnings("java:S1612")
    @AfterReturning(value = "execution(com.msquare.flabook.common.controllers.CommonResponse com.msquare.flabook.controller.PhotoController.doRead*(..) )", returning = "returnValue")
    public void readCountAdvice(JoinPoint joinPoint, CommonResponse<PostingDto> returnValue) {
        if (returnValue != null && returnValue.getResultCode() == CommonResponseCode.SUCCESS.getResultCode()) {
            Stream.of(joinPoint.getArgs()).filter(o -> o instanceof User).findFirst().ifPresent(o -> {
                if(log.isDebugEnabled()) {
                    log.debug("readCountAdvice : {}", returnValue.getResultMessage());
                }
                template.convertAndSend("postingReadQueue", new ReadMessage(returnValue.getResultMessage().getId(), ((User) o).getId()));
            });
        }
    }

    @AfterReturning(value = "execution(com.msquare.flabook.common.controllers.CommonResponse com.msquare.flabook.controller.PhotoController.doRemovePhotoPosting(..) )", returning = "returnValue")
    public void deleteS3(JoinPoint joinPoint, CommonResponse<List<PostingDto>> returnValue) {
        if (returnValue != null && returnValue.getResultCode() == CommonResponseCode.SUCCESS.getResultCode() && returnValue.getResultMsg().equals("전체")) {
            List<String> collect = returnValue.getResultMessage().stream().map(PostingDto::getAttachments).flatMap(attachmentDtos -> attachmentDtos.stream().map(dtos -> dtos.getImageResource().getFilekey())).collect(Collectors.toList());
            template.convertAndSend("deleteS3Queue", collect);
        }
    }

}
