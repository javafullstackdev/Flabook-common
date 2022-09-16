package com.msquare.flabook.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.ZonedDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@DynamicUpdate
@ToString(exclude = {"user"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "user"})
/*사용자 이메일 등록 인증 내역*/
public class UserModifyEmailHistory extends BaseEntity {

    @Id
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;

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

}
