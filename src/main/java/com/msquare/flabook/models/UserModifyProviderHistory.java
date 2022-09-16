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

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@DynamicUpdate
@Table(indexes = {@Index(name = "IDX_PROVIDER_ID", columnList = "providerId")})
@ToString(exclude = {"user"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "user"})
/*기존 휴대폰 사용자, SNS, email 계정 연동 로그 테이블*/
public class UserModifyProviderHistory extends BaseEntity {

    public static final UserModifyProviderHistory DEFAULT_SETTING = new UserModifyProviderHistory();

    @Id
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "userModifyProviderHistory", optional = false)
    private User user;

    @Column(length = 10)
    private String provider;

    @Column(length = 100)
    private String providerId;
    @Field(index = org.hibernate.search.annotations.Index.YES, analyze = Analyze.NO, store = Store.YES)
    @Column(columnDefinition="VARBINARY(40)")
    private String nickname;
}
