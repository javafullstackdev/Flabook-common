package com.msquare.flabook.repository;

import lombok.AllArgsConstructor;
import com.msquare.flabook.enumeration.EventType;
import com.msquare.flabook.enumeration.UserRole;
import com.msquare.flabook.models.Notification;
import com.msquare.flabook.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class NotificationRepositoryCustom {

    private static final String EVENT_TYPE_PATH = "eventType";
    private final EntityManager em;

    public List<Notification> findUnreadGlobalNotificationList(User user, Optional<Long> optionalNotificatioId) {
        Long userId = user.getId();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Notification> query = cb.createQuery(Notification.class);

        Root<Notification> notificationRoot = query.from(Notification.class);
        MapJoin<Long, Boolean, Notification> joinRecipients = notificationRoot.joinMap("recipients", JoinType.LEFT);
        joinRecipients.on(cb.equal(joinRecipients.key(), userId));
        Path<Long> id = notificationRoot.get("id");
        query.select(notificationRoot);
        List<Predicate> eventTypeFilter = new ArrayList<>();
        eventTypeFilter.add(cb.equal(notificationRoot.get(EVENT_TYPE_PATH), EventType.ADMIN_CUSTOM_ALL));
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(
                cb.or(
                        eventTypeFilter.toArray(new Predicate[]{})
                )
        );

        predicates.add(cb.isNull(joinRecipients.key()));
        optionalNotificatioId.ifPresent(notificationId -> predicates.add(cb.equal(id, notificationId)));

        query.where(predicates.toArray(new Predicate[]{})).orderBy(cb.desc(id));
        return em.createQuery(query).getResultList();
    }


    public  List<Tuple> findList(User user, Long sinceId, Long maxId, int count) {
        Long userId = user.getId();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<Notification> notificationRoot = query.from(Notification.class);
        MapJoin<Long, Boolean, Notification> joinRecipients = notificationRoot.joinMap("recipients", JoinType.LEFT);
        joinRecipients.on(cb.equal(joinRecipients.key(), userId));
        Path<Long> id = notificationRoot.get("id");

        query.multiselect(notificationRoot, joinRecipients.value());


        List<Predicate> eventTypeFilter = new ArrayList<>();

        eventTypeFilter.add(cb.equal(joinRecipients.key(), userId));
        eventTypeFilter.add(cb.equal(notificationRoot.get(EVENT_TYPE_PATH), EventType.NEW_AD));
        eventTypeFilter.add(cb.equal(notificationRoot.get(EVENT_TYPE_PATH), EventType.ADMIN_CUSTOM_ALL));
        if(UserRole.EXPERT.equals(user.getRole())) {
            eventTypeFilter.add(cb.equal(notificationRoot.get(EVENT_TYPE_PATH), EventType.ADMIN_CUSTOM_EXPERT));
        }

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(
            cb.or(
            // 광고는 모두 포함
                    eventTypeFilter.toArray(new Predicate[]{})
            )
        );

        if (maxId != null && maxId > 0) predicates.add(cb.le(id, maxId));
        if (sinceId != null && sinceId > 0) predicates.add(cb.gt(id, sinceId));

        query.where(predicates.toArray(new Predicate[]{})).orderBy(cb.desc(id));

        return em.createQuery(query).setMaxResults(count).getResultList();

    }
}
