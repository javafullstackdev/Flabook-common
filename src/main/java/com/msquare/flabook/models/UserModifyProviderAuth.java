package com.msquare.flabook.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;
import java.time.ZonedDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@DynamicUpdate
@ToString(exclude = {"user"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "user"})
/*기존 휴대폰 사용자, 이메일 계정으로 로그인 변환 시 인증을 위한 테이블*/
public class UserModifyProviderAuth extends BaseEntity {

    @Id
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;

    @Field(index = org.hibernate.search.annotations.Index.YES, analyze = Analyze.NO, store = Store.YES)
    @Column(columnDefinition="VARBINARY(40)")
    private String nickname;

	/*
	 * 이메일 가입자, 인증번호 확인 여부
	 */
    @Column(length = 50)
    private String email;

    @Column(length = 6)
    private String authKey; //인증번호

    @Column(columnDefinition="boolean default true")
    private Boolean authStatus = true; //이메일 인증 여부

    private ZonedDateTime sendedAt; //인증번호 발송 시간

    private String password;

}
