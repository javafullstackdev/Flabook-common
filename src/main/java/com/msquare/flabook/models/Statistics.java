package com.msquare.flabook.models;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.ZonedDateTime;

@EqualsAndHashCode(callSuper=false)
@Data
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
public class Statistics extends BaseEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    @NonNull
    //한국시간 기준
    private ZonedDateTime dt;

    @Column(name="question_count", columnDefinition = "int default 0 comment '질문게시글수'")
    private Integer questionCount = 0;

    @Column(name="magazine_count", columnDefinition = "int default 0 comment '매거진게시글수'")
    private Integer magazineCount = 0;

    @Column(name="free_count", columnDefinition = "int default 0 comment '자유게시글수'")
    private Integer freeCount = 0;

    @Column(name="boast_count", columnDefinition = "int default 0 comment '자랑게시글수'")
    private Integer boastCount = 0;

    @Column(name="guidebook_count", columnDefinition = "int default 0 comment '가이드북게시글수'")
    private Integer guidebookCount = 0;

    @Column(name="clinic_count", columnDefinition = "int default 0 comment '클리닉게시글수'")
    private Integer clinicCount = 0;


    @Column(name="comments_count", columnDefinition = "int default 0 comment '답변수'")
    private Integer commentsCount = 0;

    @Column(name="latereply_count_10", columnDefinition = "int default 0 comment '지연답변(10분이상)'")
    private Integer lateReplyCount10 = 0;

    @Column(name="latereply_count_30", columnDefinition = "int default 0 comment '지연답변(30분이상)'")
    private Integer lateReplyCount30 = 0;

    @Column(name="dau", columnDefinition = "int default 0 comment '활동자수(글게시, 답변 작성)'")
    private Integer dau = 0;

    @Column(name="leave_count", columnDefinition = "int default 0 comment '탈퇴수'")
    private Integer leaveCount = 0;

    @Column(name="login_count", columnDefinition = "int default 0 comment '로그인수'")
    private Integer loginCount = 0;

    //계정 os별
    @Column(name="android_join_count", columnDefinition = "int default 0 comment '안드로이드 가입수'")
    private Integer androidJoinCount = 0;
    @Column(name="cum_android_join_count", columnDefinition = "int default 0 comment '누적 안드로이드 가입수'")
    private Integer cumAndroidJoinCount = 0;

    @Column(name="ios_join_count", columnDefinition = "int default 0 comment 'IOS 가입수'")
    private Integer iosJoinCount = 0;
    @Column(name="cum_ios_join_count", columnDefinition = "int default 0 comment '누적 IOS 가입수'")
    private Integer cumIosJoinCount = 0;

    @Column(name="etc_join_count", columnDefinition = "int default 0 comment '기타 가입수'")
    private Integer etcJoinCount = 0;
    @Column(name="cum_etc_join_count", columnDefinition = "int default 0 comment '누적 기타 가입수'")
    private Integer cumEtcJoinCount = 0;


    //계정 os별 End

    //계정 provider별
    @Column(name="naver_join_count", columnDefinition = "int default 0 comment '네이버 가입수'")
    private Integer naverJoinCount = 0;

    @Column(name="cum_naver_join_count", columnDefinition = "int default 0 comment '누적 네이버 가입수'")
    private Integer cumNaverJoinCount = 0;

    @Column(name="kakao_join_count", columnDefinition = "int default 0 comment '카카오 가입수'")
    private Integer kakaoJoinCount = 0;

    @Column(name="cum_kakao_join_count", columnDefinition = "int default 0 comment '누적 카카오 가입수'")
    private Integer cumKakaoJoinCount = 0;

    @Column(name="facebook_join_count", columnDefinition = "int default 0 comment '페이스북 가입수'")
    private Integer facebookJoinCount = 0;

    @Column(name="cum_facebook_join_count", columnDefinition = "int default 0 comment '누적 페이스북 가입수'")
    private Integer cumFacebookJoinCount = 0;

    @Column(name="apple_join_count", columnDefinition = "int default 0 comment '애플 가입수'")
    private Integer appleJoinCount = 0;

    @Column(name="cum_apple_join_count", columnDefinition = "int default 0 comment '누적 애플 가입수'")
    private Integer cumAppleJoinCount = 0;

    @Column(name="email_join_count", columnDefinition = "int default 0 comment '이메일 가입수'")
    private Integer emailJoinCount = 0;

    @Column(name="cum_email_join_count", columnDefinition = "int default 0 comment '누적 이메일 가입수'")
    private Integer cumEmailJoinCount = 0;

    @Column(name="phone_join_count", columnDefinition = "int default 0 comment '전화번호 가입수'")
    private Integer phoneJoinCount = 0;

    @Column(name="cum_phone_join_count", columnDefinition = "int default 0 comment '누적 전화번호 가입수'")
    private Integer cumPhoneJoinCount = 0;

    @Column(name="share_count", columnDefinition = "int default 0 comment '컨텐츠 공유수'")
    private Integer shareCount = 0;

    @Column(name="scrap_count", columnDefinition = "int default 0 comment '컨텐츠 스크랩수'")
    private Integer scrapCount = 0;

    @Column(name="like_posting_count", columnDefinition = "int default 0 comment '컨텐츠 좋아요수'")
    private Integer likePostingCount = 0;

    //계정 provider별 End


    @Column(columnDefinition = "bigint default 0")
    private Long firstPostingId;
    @Column(columnDefinition = "bigint default 0")
    private Long lastPostingId;

    @Column(columnDefinition = "bigint default 0")
    private Long firstCommentId;
    @Column(columnDefinition = "bigint default 0")
    private Long lastCommentId;

    @Column(columnDefinition = "bigint default 0")
    private Long firstUserId;
    @Column(columnDefinition = "bigint default 0")
    private Long lastUserId;

    @Column(columnDefinition = "bigint default 0")
    private Long firstShareId;
    @Column(columnDefinition = "bigint default 0")
    private Long lastShareId;

    @Column(columnDefinition = "bigint default 0")
    private Long firstLikeId;
    @Column(columnDefinition = "bigint default 0")
    private Long lastLikeId;

    @Column(columnDefinition = "bigint default 0")
    private Long firstScrapId;
    @Column(columnDefinition = "bigint default 0")
    private Long lastScrapId;

    @Column(columnDefinition = "bigint default 0")
    private Long firstLeaveId;
    @Column(columnDefinition = "bigint default 0")
    private Long lastLeaveId;

    @Column(columnDefinition = "bigint default 0")
    private Long firstLoginHistoryId;
    @Column(columnDefinition = "bigint default 0")
    private Long lastLoginHistoryId;


}
