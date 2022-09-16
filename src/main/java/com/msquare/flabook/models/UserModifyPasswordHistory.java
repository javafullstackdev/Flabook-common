package com.msquare.flabook.models;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@DynamicUpdate
@ToString(exclude = {"user"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "user"})
/*사용자 비밀번호 변경 개인화 URL, 메일 발송 내역*/
public class UserModifyPasswordHistory extends BaseEntity {

    public static final UserModifyPasswordHistory DEFAULT_SETTING = new UserModifyPasswordHistory();

    @Id
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;

    @Column(length = 50)
    private String authKey; //인증번호

    @Column(columnDefinition="boolean default false")
    private Boolean authStatus = false; //패스워드 변경 여부

}
