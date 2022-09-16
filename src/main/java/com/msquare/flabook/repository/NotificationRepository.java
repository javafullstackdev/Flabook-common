package com.msquare.flabook.repository;

import com.msquare.flabook.models.Notification;
import com.msquare.flabook.models.Resource;
import com.msquare.flabook.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("select n from Notification n join n.recipients r where n.resource.referenceId = :#{#reference.referenceId} and n.resource.referenceType = :#{#reference.referenceType} and key(r) = :userId")
    public Notification findNotificationByReferenceAndUserId(@Param("reference") Resource reference, @Param("userId") Long userId);

    @Query("select n from Notification n join n.recipients r where n.resource.referenceId = :#{#reference.referenceId} and n.resource.referenceType = :#{#reference.referenceType}")
    public Notification findNotificationByReference(@Param("reference") Resource reference);


    @Modifying
//    @Query("delete r from Notification n join n.recipients r  where n.resource.resourceId = :resourceId and n.resource.resourceType = :resourceType and r in (:recipients)")
    @Query(value = "delete nu from notification_recipient nu left join notification n " +
            "on nu.notification_id = n.id where n.reference_type = :#{#reference.referenceType.ordinal()} and n.reference_id = :#{#reference.referenceId} and nu.user_id in (:recipients)", nativeQuery = true)
    public void deleteNotificationByReference(@Param("reference") Resource reference, @Param("recipients") List<Long> recipients);


    @Modifying
    @Query(nativeQuery = true, value = "delete n from  notification n join notification_recipient nu  on nu.notification_id = n.id where n.resource_type = :#{#reference.referenceType.ordinal()} and n.reference_id = :#{#reference.referenceId} and nu.user_id is null")
    public void deleteNotificationNoRecipient(@Param("reference") Resource reference);

    @Query("select n from Notification n join n.recipients r where key(r) = :userId")
    public List<Notification> findList(@Param("userId") long userId, Pageable pageable);

    @Modifying
    @Query( nativeQuery = true, value= "update notification n join notification_recipient nu on n.id = nu.notification_id set nu.unread = false where nu.user_id = :#{#user.id} and n.id = :id ")
    public void updateMarkRead(@Param("id") long id, @Param("user") User user);

    @Modifying
    @Query( nativeQuery = true, value= "insert into notification_recipient(notification_id, user_id, unread) values(:id, :#{#user.id}, false)")
    public void insertMarkRead(@Param("id") long id, @Param("user") User user);

    @Modifying
    @Query( nativeQuery = true, value= "update notification n join  notification_recipient nu on n.id = nu.notification_id set nu.unread = false where nu.user_id = :#{#user.id}")
    public void updateMarkAllRead(@Param("user")  User user);



    /**
     * 지정날짜 기준 가장 최근
     * @param datetime
     */
    @Query( nativeQuery = true, value = "select a.id from (select id from notification where created_at <= :datetime order by id desc limit 1) a")
    public BigInteger findLeastRecentlyId(@Param("datetime") ZonedDateTime datetime);

    /**
     * id 이전 알림 삭제
     * @param id
     */
    @Modifying
    @Query(value = "delete from Notification n where n.id <= :id")
    public void deleteByLeastRecently(@Param("id") Long id);


    /**
     * id 이전 recipient 삭제
     * @param id
     */
    @Modifying
    @Query( nativeQuery = true, value = "delete from notification_recipient where notification_id <= :id")
    public void deleteRecipientsByLeastRecently(@Param("id") Long id);

    /**
     * 최신 limit 갯수만큼을 제외한 나머지 삭제
     * @param userId
     * @param limit
     * @return
     */
    @Modifying
    @Query(nativeQuery = true, value="delete from notification_recipient where notification_id < " +
            "(select min(id) from (" +
            "        select n.id as id" +
            "        from notification n" +
            "                 join notification_recipient nr on n.id = nr.notification_id " +
            "        where nr.user_id = :userId " +
            "        order by id desc " +
            "        limit :limit " +
            "        ) a) and user_id = :userId")
    public void deleteByLimit(@Param("userId")  Long userId, @Param("limit") int limit);

    @Query(nativeQuery = true, value = "select * from notification limit 1")
    public Notification findFirst();
}
