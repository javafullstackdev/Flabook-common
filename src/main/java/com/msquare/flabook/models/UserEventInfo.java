package com.msquare.flabook.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@DynamicInsert
@DynamicUpdate
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEventInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //!!!!중요!!!! OneToOne join 의 경우 optional = true 일 경우 left outer join 으로 조회시 index 를 음 안탈 수 있음
    @OneToOne(mappedBy = "eventInfo", optional = false)
    private User user;

    @Column(columnDefinition="varchar(20) comment '이름'")
    private String name;

    @Column(columnDefinition="varchar(20) comment '전화번호1'")
    private String phone1;
    @Column(columnDefinition="varchar(20) comment '전화번호2'")
    private String phone2;

    @Column(columnDefinition="varchar(80) comment '도로명주소'")
    private String roadAddress;
    @Column(columnDefinition="varchar(10) comment '우편번호'")
    private String postCode;
    @Column(columnDefinition="varchar(45) comment '도로명주소'")
    private String detailAddress;
}
