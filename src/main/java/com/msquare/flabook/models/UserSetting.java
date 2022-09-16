package com.msquare.flabook.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.ZonedDateTime;

@EqualsAndHashCode(callSuper=false)
@Data
@Entity
@NoArgsConstructor
@DynamicUpdate
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "user"})
public class UserSetting extends BaseEntity {

    public static final UserSetting DEFAULT_SETTING = new UserSetting();

    @Id
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 일반 알림(공지, 앱알림) 상태
     * - DEFAULT_TOPIC 구독 여부
     */
    @Getter
    @Setter
    //알림수신여부
    @Column(columnDefinition = "bit default 1 comment '알림수신여부'")
    private boolean notiEnable = true;

    @Getter
    @Setter
    //추천게시물알림수신여부
    @Column(columnDefinition = "bit default 1 comment '추천게시물알림수신여부'")
    private boolean postingNotiEnable = true;

    @Getter
    @Setter
    //댓글알림수신여부
    @Column(columnDefinition = "bit default 1 comment '댓글알림수신여부'")
    private boolean commentNotiEnable = true;

    @Getter
    @Setter
    //답글알림수신여부
    @Column(columnDefinition = "bit default 1 comment '답글알림수신여부'")
    private boolean replyNotiEnable = true;

    @Getter
    @Setter
    //연급알림수신여부
    @Column(columnDefinition = "bit default 1 comment '연급알림수신여부'")
    private boolean mentionNotiEnable = true;

    @Getter
    @Setter
    //결제,배송정보
    @Column(columnDefinition = "bit default 1 comment '결제,배송정보알림수신여부'")
    private boolean shopNotiEnable = true;

    @Getter
    @Setter
    //좋아요알림수신여부
    @Column(columnDefinition = "bit default 1 comment '좋아요알림수신여부'")
    private boolean likeNotiEnable = true;


    @Getter
    @Setter
    @Column(columnDefinition = "bit default 1 comment '채택알림수신여부'")
    private boolean adoptNotiEnable = true;

    @Getter
    @Setter
    @Column(columnDefinition = "bit default 1 comment '뱃지알림수신여부'")
    private boolean badgeNotiEnable = true;



    @Getter
    @Setter
    //광고알림수신여부
    @Column(columnDefinition = "bit default 1 comment '광고알림수신여부'")
    private boolean adNotiEnable = true;

    //광고알림 업데이트 일자
    private ZonedDateTime adNotiUpdatedAt;

    //광고알림수신 확인 일자
    private ZonedDateTime adNotiConfirmedAt;

    //광고알림수신여부보다 상위 조건
    @Column(columnDefinition = "bit comment '광고알림동의여부'")
    private Boolean adNotiAgreement;

    //참여한 댓글 알림 모두받기 설정
    @Column(columnDefinition = "bit default 0 comment '참여댓글알림모두받기여부'")
    private boolean joinCommentNotiEnable = false;


    @Setter
    //포토 접근 권한
    @Column(columnDefinition = "bit")
    private Boolean photoEnable = null;

    public Boolean getPhotoEnable() {
        return BooleanUtils.isTrue(photoEnable);
    }
}
