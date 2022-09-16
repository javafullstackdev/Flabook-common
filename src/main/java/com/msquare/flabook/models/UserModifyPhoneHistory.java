package com.msquare.flabook.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.ZonedDateTime;

@EqualsAndHashCode(callSuper=false)
@Data
@Entity
@NoArgsConstructor
@DynamicUpdate
@ToString(exclude = {"user"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "user"})
/*사용자 휴대폰 번호 등록 인증 내역*/
public class UserModifyPhoneHistory extends BaseEntity {

    @Id
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;


    /*
	 * 휴대폰번호 가입자
	 */
    @Column(length = 11)
    private String phoneNumber; //휴대폰 번호
    @Column(length = 6)
    private String authKey; //휴대폰 번호 인증번호
    @Column(columnDefinition="boolean default true")
    private Boolean authStatus = true; //이메일 인증 여부
    private ZonedDateTime sendedAt; //휴대폰 번호 인증번호 발송 시간

}
