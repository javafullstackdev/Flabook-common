package com.msquare.flabook.repository;

import com.msquare.flabook.enumeration.ExpertGroup;
import com.msquare.flabook.enumeration.UserRole;
import com.msquare.flabook.enumeration.UserStatus;
import com.msquare.flabook.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query(value = "UPDATE User u set u.activity.postingCount = u.activity.postingCount + 1 where u.id = :id")
    int updateIncrementPostingCount(@Param("id") long id);

    @Modifying
    @Query(value = "UPDATE User u set u.activity.postingCount = u.activity.postingCount - 1 where u.id = :id")
    int updateDecrementPostingCount(@Param("id") long id);


    @Modifying
    @Query(value = "UPDATE User u set u.activity.commentCount = u.activity.commentCount + 1 where u.id = :id")
    int updateIncrementCommentCount(@Param("id") long id);

    @Modifying
    @Query(value = "UPDATE User u set u.activity.adoptedCount = u.activity.adoptedCount + 1 where u.id = :id")
    int updateIncrementAdoptedCount(@Param("id") long id);

    @Modifying
    @Query(value = "UPDATE User u set u.level = :level where u.id = :id")
    int updateLevel(@Param("id") long id, @Param("level") int level);


    @Modifying
    @Query(value = "UPDATE User u set u.activity.commentCount = u.activity.commentCount - 1 where u.id = :id")
    int updateDecrementCommentCount(@Param("id") long id);

    List<User> findByNicknameAndStatusNotAndSecurityAuthStatus(String nickname, UserStatus status, Boolean authStatus); //유저권한만 체크?

    //kuh:20200915, 휴대번호 provider_id 중복으로 인한 유니크 에러 패치
    @Query( nativeQuery = true, value="SELECT * FROM (SELECT * FROM user WHERE provider_id = :providerId AND provider = :provider ORDER BY id DESC) A LIMIT 0, 1")
    Optional<User> findByProviderIdAndProvider(@Param("providerId") String providerId, @Param("provider") String provider);

    List<User> findByProviderId(@Param("providerId") String providerId);

    @SuppressWarnings("unused")
    Optional<User> findByNicknameAndProvider(String nickname, String provider);
    @SuppressWarnings("unused")
    Optional<User> findBySecurityAccessToken(String accessToken);
    Optional<User> findBySecurityRefreshToken(String refreshToken);

    /**
     *
     * @param userIds 사용자 id목록
     * @return List<Tuple> [user_id, os_type, token, unread_count]
     */
    @Query( nativeQuery = true, value= "select u.id as `user_id`, t.os_type as `os_type`, t.token as `token` , count(if(unread = true, 1, null)) as `badge` , count(*) as `cnt` from notification n inner join notification_recipient nu on n.id = nu.notification_id inner join user u on nu.user_id = u.id inner join user_push_token t on u.id = t.user_id " +
            " where u.id in(:userIds)" +
            " group by t.os_type, token, u.id")
    public List<Tuple> findUserPushTokenWithOsTypeAndBadges(@Param("userIds") Set<Long> userIds);


    /*
     * For Admin
     */
    public List<User> findAllByRole(Pageable pageable, UserRole userRole);
    Long countByRole(UserRole userRole);

    public List<User> findAllByExpertGroupExpertGroupAndStatus(ExpertGroup expertGroup, UserStatus status);

    //비밀번호 찾기, 개인화 URL로 받은 authKey를 통해 user 패스워드 변경 요청 내역 찾기
    Optional<User> findByuserModifyPasswordHistoryAuthKey(String authKey);

    /**
     * 계정 이전 내역 조회
     * @param providerId 가입 id
     * @return 조회 목록 List<Tuple> [history id, user id]
     */
    @Query(value = "SELECT h.id , u.id FROM UserModifyProviderHistory h join h.user u WHERE h.providerId = :providerId")
    List<Tuple> findModifiedUserByProviderId(@Param(value = "providerId") String providerId);

    @Query(value = "select u.id from User u where u.security.id = :securityId")
    Long findIdBySecurityId(@Param(value = "securityId") Long securityId);

    /**
     * 포토
     * */
    @Modifying
    @Query(value = "UPDATE User u set u.totalPhotosCnt = u.totalPhotosCnt + 1 where u.id = :id")
    int updateIncrementPhotoPostingCount(@Param("id") long id);

    @Modifying
    @Query(value = "UPDATE User u set u.totalPhotosCnt = u.totalPhotosCnt - 1 where u.id = :id")
    int updateDecrementPhotoPostingCount(@Param("id") long id);

    @Modifying
    @Query(value = "UPDATE User u set u.totalPhotosLikeCnt = u.totalPhotosLikeCnt + 1 where u.id = :id")
    int updateIncrementPhotoLikeCount(@Param("id") Long id);

    @Modifying
    @Query(value = "UPDATE User u set u.totalPhotosLikeCnt = u.totalPhotosLikeCnt - 1 where u.id = :id")
    int updateDecrementPhotoLikeCount(@Param("id") Long id);

    @Modifying
    @Query(value = "UPDATE User u set u.totalPhotosLikeCnt = u.totalPhotosLikeCnt - :count where u.id = :id")
    int updateDecrementPhotoLikeCount(@Param("id") Long id, @Param("count") int count);
}
