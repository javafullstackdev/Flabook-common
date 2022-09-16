package com.msquare.flabook.models;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString(of = {"id", "name", "amount", "remains"})
@Entity
public class GambleItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(255) default '' comment '제목'")
    private String name;

    @Column(columnDefinition = "int default '0' comment '기본값'")
    private int amount;

    @Column(columnDefinition = "int default '0' comment '잔량'")
    private int remains;

    @Column(columnDefinition = "bit default 0 comment '주소입력여부'")
    private boolean address;

    @ManyToOne(fetch = FetchType.LAZY)
    private Gamble gamble;

    @Column(columnDefinition = "bit default 0 comment '꽝여부'")
    private boolean blank;
}
