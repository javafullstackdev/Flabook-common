package com.msquare.flabook.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.msquare.flabook.json.Views;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
//@Embeddable
@NoArgsConstructor
@Entity
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "user"})
@DynamicUpdate
@JsonView(Views.IgnoreJsonView.class)
@Table(
	indexes = {
		@Index(name = "IDK_REFRESH_TOKEN", columnList = "refreshToken")}
	,uniqueConstraints = {
	        @UniqueConstraint(name = "UK_ACCESS_TOKEN", columnNames = {"accessToken"})
	})
public class UserSecurity implements Serializable {
	@Id
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(length = 40)
	private String email;
	@Column(length = 30)
    private String phoneNumber;

    private String password;

    @Column(columnDefinition="VARBINARY(255)")
    private String accessToken;

    private ZonedDateTime accessTokenExpireAt;

    @Column(columnDefinition="VARBINARY(255)")
    private String refreshToken;

    private ZonedDateTime refreshTokenExpireAt;

    private String salt;
	/*
	 * 휴대폰번호 가입자
	 */
    //private String authId; //휴대폰 번호 -> providerId로 변경
    @Column(length = 6)
    private String authIdKey; //휴대폰 번호 인증번호
    private ZonedDateTime authIdKeySendedAt; //휴대폰 번호 인증번호 발송 시간

	/*
	 * 이메일 가입자, 인증번호 확인 여부
	 */
    @Column(columnDefinition="boolean default true")
    private Boolean authStatus = true; //이메일 인증 여부
    @Column(length = 6)
    private String authKey; //이메일 인증번호
    private ZonedDateTime authMailSendedAt; //이메일 인증번호 발송 시간

    /**
     * 관리자 메모
     */
    private String memo;

	/*탈퇴한 사용자*/
    private String deleteReason; //탈퇴 사유
}
