package com.msquare.flabook.models;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = false)
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
@Table(uniqueConstraints = {@UniqueConstraint(name = "UK_DATE", columnNames = {"dt"})})
public class StatisticsDaily extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private LocalDate dt;

    //신규회원수
    @Column(columnDefinition = "int default 0 comment '가입자수'")
    private int signUpCount;

    //누적회원수
    @Column(columnDefinition = "int default 0 comment '누적회원수'")
    private int accumSignUpCount;

    //신규 알림 거부수
    @Column(columnDefinition = "int default 0 comment '신규 알림 거부수'")
    private int notiOffCount;

    //누적 알림 거부수
    @Column(columnDefinition = "int default 0 comment '누적 알림 거부수'")
    private int accumNotiOffCount;

    //게시글
    @Column(columnDefinition = "int default 0 comment '이름이 뭐야 게시글수'")
    private int questionCount;
    @Column(columnDefinition = "int default 0 comment '자랑하기 게시글수'")
    private int boastCount;
    @Column(columnDefinition = "int default 0 comment '매거진 게시글수'")
    private int magazineCount;
    @Column(columnDefinition = "int default 0 comment '자유게시판 게시글수'")
    private int freeCount;
    @Column(columnDefinition = "int default 0 comment '가이드 게시글수'")
    private int guideCount;
    @Column(columnDefinition = "int default 0 comment '클리닉 게시글수'")
    private int clinicCount;

    //댓글
    @Column(columnDefinition = "int default 0 comment '댓글수'")
    private int commentCount;
    //좋아요
    @Column(columnDefinition = "int default 0 comment '좋아요수'")
    private int likeCount;
    //북마크
    @Column(columnDefinition = "int default 0 comment '북마크수'")
    private int bookmarkCount;
    //공유
    @Column(columnDefinition = "int default 0 comment '공유수'")
    private int shareCount;

    //그룹별 접속자
    @Column(columnDefinition = "int default 0 comment '일반사용자 접속수'")
    private int userSignInCount;

    @Column(columnDefinition = "int default 0 comment '전문가 접속수'")
    private int expertSignInCount;

    @Column(columnDefinition = "int default 0 comment '컨텐츠 전문가 접속수'")
    private int contentsExpertSignInCount;

    @Column(columnDefinition = "int default 0 comment '클리닉 전문가 접속수'")
    private int clinicExpertSignInCount;

    @Column(columnDefinition = "int default 0 comment '식물이름 전문가 접속수'")
    private int nameExpertSignInCount;



    @Column(columnDefinition = "bigint default 0")
    private Long lastPostingId;
    @Column(columnDefinition = "bigint default 0")
    private Long lastCommentId;
    @Column(columnDefinition = "bigint default 0")
    private Long lastUserId;
    @Column(columnDefinition = "bigint default 0")
    private Long lastShareId;
    @Column(columnDefinition = "bigint default 0")
    private Long lastLikeId;
    @Column(columnDefinition = "bigint default 0")
    private Long lastScrapId;

}
