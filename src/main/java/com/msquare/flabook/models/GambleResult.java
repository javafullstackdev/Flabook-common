package com.msquare.flabook.models;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * 이벤트 당첨여부
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(of = {"id", "item", "gamble"})
@Builder
@Entity
public class GambleResult implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private GambleItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    private Gamble gamble;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(columnDefinition = "datetime comment '시간'")
    private ZonedDateTime createdAt;

    @Column(columnDefinition = "bit default 0 comment '주소입력 필요여부'")
    private Boolean needAddress;

    @Column(columnDefinition = "int default 0 comment 'gamble version 확인용'")
    private int version;

}
