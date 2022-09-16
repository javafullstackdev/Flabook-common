package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.json.Views;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.BaseView.class)
public class EmailVerifyInfoDto implements Serializable {

    private String authKey; //휴대폰 번호 인증번호
    private ZonedDateTime authMailSendedAt; //휴대폰 번호 인증번호 발송 시간

}
