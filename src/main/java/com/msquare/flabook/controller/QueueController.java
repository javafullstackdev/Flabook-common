package com.msquare.flabook.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.domain.badge.NewBadge;
import com.msquare.flabook.models.UserLoginHistory;
import com.msquare.flabook.queue.*;
import com.msquare.flabook.repository.UserLoginHistoryRepository;
import com.msquare.flabook.service.BadgeService;
import com.msquare.flabook.service.ImageUploadService;
import com.msquare.flabook.service.PostingService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * PostingAspect 를 통해 전달된 데이터를 게시글 별로 그룹핑하고 읽음 수 업데이트
 * 상태 변경도 진행함
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(path = "/v2/histories")
public class QueueController {

    private final AmqpTemplate template;
    private final PostingService postingService;
    private final BadgeService badgeService;
    private final UserLoginHistoryRepository userLoginHistoryRepository;
    private final ImageUploadService imageUploadService;

    @RabbitListener(queues = "postingReadQueue", containerFactory = "rabbitListenerContainerFactory")
    public void aggregate(List<ReadMessage> in) {
        Map<Long, Long> counting = in.stream().collect(Collectors.groupingBy(ReadMessage::getPostingId, Collectors.counting()));
        counting.forEach((postingId, readCount) -> template.convertAndSend("updateReadQueue", new GroupingMessage(postingId, readCount)));
    }

    @Transactional
    @RabbitListener(queues = "loginQueue", containerFactory = "rabbitListenerContainerFactory")
    public void loginMessageListener(List<LoginMessage> in) {

        log.info("loginMessageListener");
        Map<Long, Long> counting = in.stream().collect(Collectors.toMap(LoginMessage::getUserId, LoginMessage::getTimestamp, (a, b) -> (a.compareTo(b) > 0) ? a : b));

        String tokenStr = "{LOGIN}";
        List<UserLoginHistory> loginHistories = counting.entrySet().stream().map(entry -> {
            UserLoginHistory userLoginHistory = new UserLoginHistory();
            userLoginHistory.setUserId(entry.getKey());
            userLoginHistory.setAccessToken(tokenStr);
            userLoginHistory.setCreatedAt(ZonedDateTime.ofInstant(Instant.ofEpochSecond(entry.getValue()), ZoneId.of("UTC")));
            return userLoginHistory;
        }).collect(Collectors.toList());

        userLoginHistoryRepository.saveAll(loginHistories);
    }

    @RabbitListener(bindings =
        @QueueBinding(exchange = @Exchange(value = "issue-badge"), value = @Queue(value = "issue-badge"))
    , containerFactory = "rabbitListenerContainerFactory")
    public void issueBadge(List<NewBadge> in) {

        Map<Long, List<NewBadge>> groupByOwner = in.stream().collect(Collectors.groupingBy(NewBadge::getOwner, Collectors.toList()));
        groupByOwner.forEach((key, value) -> {
            Set<Long> badgeIds = value.stream().flatMap(newBadge -> newBadge.getBadges().stream()).map(Long::parseLong).collect(Collectors.toSet());
            log.info("issue user : {}, badgeIds : {}", key, badgeIds);
            badgeService.addUserBadges(key, badgeIds);
        });

    }

    @RabbitListener(queues = "postingReceivingQueue", containerFactory = "rabbitListenerContainerFactory")
    public void aggregate2(List<ReceivingMessage> in) {

        Map<Long, Long> counting = in.stream().flatMap(receivingMessage -> receivingMessage.getPostingIds().stream()).collect(Collectors.groupingBy(id -> id, Collectors.counting()));
        counting.forEach((postingId, readCount) -> template.convertAndSend("updateReadQueue", new GroupingMessage(postingId, readCount)));
    }

    @RabbitListener(queues = "searchQueryQueue", containerFactory = "rabbitListenerContainerFactory")
    public void aggregate3(List<SearchQueryMessage> in) {

        Map<String, Long> counting = in.stream().collect(Collectors.groupingBy(SearchQueryMessage::getQuery, Collectors.counting()));
        counting.forEach((query, count) -> template.convertAndSend("updateSearchQueryQueue", new SearchGroupingMessage(query, count)));
    }

    @RabbitListener(queues = "updateReadQueue", containerFactory = "rabbitListenerContainerFactory2")
    public void update(GroupingMessage message) {
        postingService.updateRead(message.getPostingId(), message.getCount());
        log.debug("Receive ReadGroupingMessage : {}", message);
    }

    @RabbitListener(queues = "updateReceivingQueue", containerFactory = "rabbitListenerContainerFactory2")
    public void update2(GroupingMessage message) {
        postingService.updateReceiving(message.getPostingId(), message.getCount());
        log.debug("Receive ReceivingGroupingMessage : {}", message);
    }

    @RabbitListener(queues = "deleteS3Queue", containerFactory = "rabbitListenerContainerFactory2")
    public void deleteS3QueueListener(List<String> keys) {
        for(String key : keys) {
            imageUploadService.delete(key);
        }
    }

}
