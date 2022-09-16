package com.msquare.flabook.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.mapper.UserSettingMapper;
import com.msquare.flabook.models.UserSetting;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSettingDto {

    //최상위 알림설정
    private Boolean notiEnable;

    private Boolean adNotiEnable;
    private Boolean likeNotiEnable;
    private Boolean postingNotiEnable;
    private Boolean replyNotiEnable;
    private Boolean mentionNotiEnable;
    private Boolean commentNotiEnable;
    private Boolean shopNotiEnable;
    private Boolean joinCommentNotiEnable;
    private Boolean adoptNotiEnable;
    private Boolean badgeNotiEnable;

    //광고알림 수신동의 일자
    private ZonedDateTime adNotiConfirmedAt;
    //광고수신 동의 여부 (adNotiEnable 와 관계없이 동의 없으면 광고 할 수 없음)
    private Boolean adNotiAgreement;

    public Boolean getAdNotiAgreement() {
        //2년마다 갱신
        if(adNotiConfirmedAt != null && adNotiConfirmedAt.plusYears(2).isBefore(ZonedDateTime.now())) {
            return null; //NOSONAR
        }

        return adNotiAgreement;
    }

    public static UserSettingDto of(UserSetting userSetting) {
        return UserSettingMapper.INSTANCE.of(userSetting);
    }
}
