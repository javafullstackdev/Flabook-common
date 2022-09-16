package com.msquare.flabook.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.dto.BadgeAfterDto;
import com.msquare.flabook.dto.NotificationDto;
import com.msquare.flabook.dto.OrderDto;
import com.msquare.flabook.enumeration.EventType;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.models.*;
import com.msquare.flabook.models.shop.Goods;
import com.msquare.flabook.notification.NotificationManager;
import com.msquare.flabook.repository.NotificationRepository;
import com.msquare.flabook.repository.NotificationRepositoryCustom;
import com.msquare.flabook.repository.WatchPostingRepository;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    @NonNull
    private final NotificationManager notificationManager;
    @NonNull
    private final NotificationRepository notificationRepository;
    @NonNull
    private final WatchPostingRepository watchPostingRepository;
    @NonNull
    private final NotificationRepositoryCustom notificationRepositoryCustom;

    @Value("${flabook.notification.deleteDuration:2592000}") // 2592000 : 30일
    private int deleteDurationSeconds;

    @SuppressWarnings("UnusedReturnValue")
    @Transactional(propagation = Propagation.MANDATORY)
    public NotificationDto afterNewComment(Comment comment, List<User> recipients) {
        return NotificationDto.of(forComment(comment, recipients, EventType.NEW_COMMENT));
    }

    @SuppressWarnings("UnusedReturnValue")
    @Transactional(propagation = Propagation.MANDATORY)
    public NotificationDto afterNewReply(Comment comment, List<User> recipients) {
        return NotificationDto.of(forComment(comment, recipients, EventType.NEW_REPLY));
    }

    @SuppressWarnings("UnusedReturnValue")
    @Transactional(propagation = Propagation.MANDATORY)
    public NotificationDto afterNewMention(Comment comment, List<User> recipients) {
        return NotificationDto.of(forComment(comment, recipients, EventType.NEW_MENTION));
    }

    @SuppressWarnings("UnusedReturnValue")
    @Transactional(propagation = Propagation.MANDATORY)
    public NotificationDto afterNewLike(LikePosting likePosting, User recipient) {
        return NotificationDto.of(forLike(likePosting, recipient));
    }

    @SuppressWarnings("UnusedReturnValue")
    @Transactional(propagation = Propagation.MANDATORY)
    public NotificationDto afterNewLike(LikeComment likeComment, User recipient) {
        return NotificationDto.of(forLike(likeComment, recipient));
    }

    @SuppressWarnings("UnusedReturnValue")
    @Transactional(propagation = Propagation.MANDATORY)
    public NotificationDto afterNewOrder(OrderDto orderDto, User recipient) {
        return NotificationDto.of(forShopOrder(orderDto, recipient));
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    @Transactional(propagation = Propagation.MANDATORY)
    public NotificationDto afterNewGoods(Goods goods, User recipient) {
        return NotificationDto.of(forShopProduct(goods, recipient));
    }

    @SuppressWarnings("UnusedReturnValue")
    @Transactional(propagation = Propagation.MANDATORY)
    public NotificationDto afterNewReply(AbstractCommentActivity comment, List<User> recipients) {
        return NotificationDto.of(forComment(comment, recipients, EventType.NEW_REPLY));
    }

    //관리자 페이지, 알림 등록
    @SuppressWarnings("UnusedReturnValue")
    @Transactional(propagation = Propagation.MANDATORY)
    public NotificationDto afterNewAdminCustom(NotificationAdmin custom, List<User> recipient, EventType eventType) {
        return NotificationDto.of(forCustom(custom, recipient, eventType)).setOsType(custom.getDeviceGroup());
    }

    @SuppressWarnings("UnusedReturnValue")
    @Transactional(propagation = Propagation.MANDATORY)
    public NotificationDto afterNewBadge(BadgeAfterDto badgeAfterDto) {
        return NotificationDto.of(forBadge(badgeAfterDto));
    }

    private Notification forCustom(NotificationAdmin custom, List<User> recipient, EventType eventType) {
        Set<User> actualRecipients = new HashSet<>(recipient);
        Notification notification = createNotification(custom, eventType, actualRecipients);
        log.debug("forCustom recipients : {}", actualRecipients);
        return notification;
    }

    @SuppressWarnings("SameParameterValue")
    private Notification forComment(AbstractCommentActivity comment, List<User> recipients, EventType eventType) {
        //언급은 watch 사용자 필요없음

        Set<User> actualRecipients = new HashSet<>(recipients);
        actualRecipients.remove(comment.getOwner());

        Notification notification = createNotification(comment, eventType, actualRecipients);
        log.debug("forComment adopted recipients : {}, eventType : {}", actualRecipients, eventType);
        return notification;
    }

    private Notification forComment(Comment comment, List<User> recipients, EventType eventType) {
        //언급은 watch 사용자 필요없음
        List<WatchPosting> watchList = (EventType.NEW_MENTION.equals(eventType)) ? Collections.emptyList() : watchPostingRepository.findWatchesByPostingId(comment.getPosting().getId());

        Set<User> actualRecipients = new HashSet<>();
        actualRecipients.addAll(recipients);
        actualRecipients.addAll(watchList.stream().filter(WatchPosting::isEnable).map(w -> w.getRelation().getUser()).distinct().collect(Collectors.toList()));
        actualRecipients.remove(comment.getOwner());

        Map<Long, WatchPosting> map = watchList.stream().collect(Collectors.toMap(w -> w.getRelation().getUser().getId(), Function.identity()));
        actualRecipients.removeIf(user -> {
            WatchPosting watch = map.get(user.getId());
            //댓글 작성과 같거나 알림그만받기 했을 경우
            return watch != null && !watch.isEnable();
        });



        Notification notification = createNotification(comment, eventType, actualRecipients);
        log.debug("forComment recipients : {}, eventType : {}", actualRecipients, eventType);
        return notification;
    }

    private Notification forLike(LikePosting likePosting, User recipient) {
        return createNotification(likePosting, EventType.NEW_LIKE_POSTING, Collections.singleton(recipient));
    }

    private Notification forLike(LikeComment likeComment, User recipient) {
        return createNotification(likeComment, EventType.NEW_LIKE_COMMENT, Collections.singleton(recipient));
    }

    private Notification forShopProduct(Goods goods, User recipient) {
        return createNotification(goods, EventType.NEW_AD, Collections.singleton(recipient));
    }

    private Notification forShopOrder(OrderDto orderDto, User recipient) {
        return createNotification(orderDto, EventType.NEW_SHOP, Collections.singleton(recipient));
    }

    private Notification forBadge(BadgeAfterDto badgeAfterDto) {
        Set<User> actualRecipients = new HashSet<>();
        actualRecipients.add(badgeAfterDto.getOwner());

        Notification notification = createNotification(badgeAfterDto, EventType.NEW_BADGE, actualRecipients);
        log.debug("forComment badge recipients : {}, eventType : {}", actualRecipients, EventType.NEW_BADGE);
        return notification;
    }

    private boolean isNotiEnable(User user, EventType eventType, INotification iNotification) {

        //전체 알림 설정 (isNotiEnable) 을 끄면 모든 알림이 안간다
        if(user.getUserSetting() == null ) return true;

        if(!user.getUserSetting().isNotiEnable()) return false;

        //광고 알림 설정에 따라 발신
        switch(eventType) {
            case NEW_AD:
            case ADMIN_CUSTOM_ALL:                 //어드멘 페이지에서 전문가들에게 알림
            case ADMIN_CUSTOM_EXPERT:                //어드멘 페이지에서 전문가들에게 알림
                return user.getUserSetting().isAdNotiEnable();

                //쇼핑 결제, 배송정보 알림 설정에 따라 발신
            case NEW_SHOP:
                return user.getUserSetting().isShopNotiEnable();
                //댓글알림 설정에 따라 발신
            case NEW_COMMENT:
                //수신자가 작성자일 경우 댓글알림설정 확인
                return user.getUserSetting().isCommentNotiEnable();

                //답글알림 설정에 따라 발신
            case NEW_REPLY:
                //수신자가 작성자일 경우 댓글알림설정 확인
                if(iNotification instanceof AdoptComment) {
                    //채택알림은 별도 설정
                    return user.getUserSetting().isAdoptNotiEnable();
                }
                return user.getUserSetting().isReplyNotiEnable();


            case NEW_MENTION:                 //댓글알림, 언급알림 설정에 따라 발신
                return user.getUserSetting().isMentionNotiEnable();

            case NEW_LIKE_POSTING: //좋아요 알림 설정에 따라 발신
            case NEW_LIKE_COMMENT: //좋아요 알림 설정에 따라 발신
                return user.getUserSetting().isLikeNotiEnable();

            case NEW_BADGE:
                return user.getUserSetting().isBadgeNotiEnable();

            default:
                // 게시글 알림 설정 여부에 따라 발신
                if(iNotification.asResource().getReferenceType().isBoardType()) {
                    return user.getUserSetting().isPostingNotiEnable();
                }
                return true;

        }
    }

    /**
     * recipients 를 대상으로 알림 목록 생성함 단 알림 해제를 한 경우 생성 안함
     *
     * @param iNotification 인터페이스
     * @param eventType 이벤트 타입
     * @param recipients 수신자 목록
     * @return Notification
     */
    private Notification createNotification(INotification iNotification, EventType eventType, Set<User> recipients) {
        Notification notification;

        switch (eventType) {
            case NEW_QUESTION :

            case NEW_COMMENT:

            case NEW_REPLY:

            case NEW_MENTION:

            case NEW_LIKE_POSTING:

            case NEW_LIKE_COMMENT:

            case NEW_AD:

            case NEW_BADGE:

            case ADMIN_CUSTOM_EXPERT:

            case ADMIN_CUSTOM_TEST:

            case ADMIN_CUSTOM_ALL:
                notification = new Notification(iNotification, eventType);
                break;

            case NEW_SHOP:

                notification = notificationRepository.findNotificationByReference(iNotification.asResource());
                if(notification != null)
                    notificationRepository.delete(notification);

                notification = new Notification(iNotification, eventType);

                break;

            default:
                throw new FlabookGlobalException("unknown eventType : " + eventType);
        }

        if(log.isDebugEnabled())
            log.debug("create Notification : {}", notification);

        Resource resource = iNotification.asResource();
        if (!CollectionUtils.isEmpty(recipients)) {
            notificationRepository.deleteNotificationByReference(resource, recipients.stream().map(User::getId).collect(Collectors.toList()));
            notificationRepository.deleteNotificationNoRecipient(resource);
        }

        notification.setRecipients(recipients.stream()
            .filter(user -> isNotiEnable(user, eventType, iNotification))
            .filter(User::isActive)
            .collect(Collectors.toMap(User::getId, x -> true)));

        //토픽 전송 이벤트이거나 수신자가 있는 경우 메시지 발신
        if(notification.getEventType().isTopic() || notification.getRecipients().size() > 0) {
            notificationRepository.save(notification);
            //push 발송
            notificationManager.sendNotification(notification, Optional.ofNullable(iNotification.getOsType()));
        }

        return notification;
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> list(User user, Long sinceId, Long maxId, int count) {
        return notificationRepositoryCustom.findList(user, sinceId, maxId, count).stream()
                .map(NotificationDto::of).collect(Collectors.toList());

    }

    @Transactional
    public void markAllRead(User user) {
        notificationRepository.updateMarkAllRead(user);
        List<Notification> unreadGlobalNotificationList = notificationRepositoryCustom.findUnreadGlobalNotificationList(user, Optional.empty());
        for(Notification unreadNotification : unreadGlobalNotificationList) {
            notificationRepository.insertMarkRead(unreadNotification.getId(), user);
        }
    }

    @Transactional
    public void markRead(User user, long id) {
        notificationRepository.updateMarkRead(id, user);
        List<Notification> unreadGlobalNotificationList = notificationRepositoryCustom.findUnreadGlobalNotificationList(user, Optional.of(id));
        for(Notification unreadNotification : unreadGlobalNotificationList) {
            notificationRepository.insertMarkRead(unreadNotification.getId(), user);
        }
    }

    @Transactional
    public void deleteByLimit(Long userId, int limit) {
        notificationRepository.deleteByLimit(userId, limit);
    }

    @Transactional
    public void deleteByLeastRecently() {
        ZonedDateTime dateTime = ZonedDateTime.now().minusSeconds(deleteDurationSeconds);
        Notification notification = notificationRepository.findFirst();
        if(notification.getCreatedAt().isAfter(dateTime))
            return;

        BigInteger bigInteger = notificationRepository.findLeastRecentlyId(dateTime);
        if(bigInteger == null)
            return;

        notificationRepository.deleteRecipientsByLeastRecently(bigInteger.longValue());
        notificationRepository.deleteByLeastRecently(bigInteger.longValue());
    }

}
