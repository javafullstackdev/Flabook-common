package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.mapper.StatisticsMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.Statistics;

import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.BaseView.class)
public class StatisticsDto {

    //한국시간 기준
    private ZonedDateTime dt;

    private Integer questionCount = 0;

    private Integer magazineCount = 0;

    private Integer freeCount = 0;

    private Integer boastCount = 0;

    private Integer guidebookCount = 0;

    private Integer clinicCount = 0;

    private Integer commentsCount = 0;

    private Integer lateReplyCount10 = 0;

    private Integer lateReplyCount30 = 0;

    private Integer dau = 0;

    private Integer leaveCount = 0;

    private Integer loginCount = 0;

    //계정 os별
    private Integer androidJoinCount = 0;
    private Integer cumAndroidJoinCount = 0;
    private Integer iosJoinCount = 0;
    private Integer cumIosJoinCount = 0;
    private Integer etcJoinCount = 0;
    private Integer cumEtcJoinCount = 0;

    //계정 os별 End

    //계정 provider별
    private Integer naverJoinCount = 0;

    private Integer cumNaverJoinCount = 0;

    private Integer kakaoJoinCount = 0;

    private Integer cumKakaoJoinCount = 0;

    private Integer facebookJoinCount = 0;

    private Integer cumFacebookJoinCount = 0;

    private Integer appleJoinCount = 0;

    private Integer cumAppleJoinCount = 0;

    private Integer emailJoinCount = 0;

    private Integer cumEmailJoinCount = 0;

    private Integer phoneJoinCount = 0;

    private Integer cumPhoneJoinCount = 0;

    public static StatisticsDto of(Statistics statistics) {
        return StatisticsMapper.INSTANCE.of(statistics);
    }
}
