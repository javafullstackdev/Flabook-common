package com.msquare.flabook.form;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSettingVo {

    //앱 설정 없음
    private Boolean notiEnable;
    //광고 푸시 설정 여부
    private Boolean adNotiEnable;
    //추천 게시글 푸시 설정여부
    private Boolean postingNotiEnable;
    //댓글 푸시 설정여부
    private Boolean commentNotiEnable;
    //대댓글 푸시 설정여부
    private Boolean replyNotiEnable;
    //쇼핑몰 푸시 설정여부
    private Boolean shopNotiEnable;
    //언급 푸시 설정여부
    private Boolean mentionNotiEnable;
    //좋아요 푸시 설정여부
    private Boolean likeNotiEnable;
    //광고 수신동의여부
    private Boolean adNotiAgreement;
    //참여한 게시글의 댓글 알림여부
    private Boolean joinCommentNotiEnable;

    //채택 수신동의여부
    private Boolean adoptNotiEnable;
    //뱃지 수신동의여부
    private Boolean badgeNotiEnable;
}
