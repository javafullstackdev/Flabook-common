package com.msquare.flabook.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * 통계 처리를 위해 날짜별, Entity 별 (min, max) Id 를 관리함
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(indexes = {
        @Index(name = "IDX_DATE", columnList = "dt"),
        @Index(name = "IDX_CLASSNAME", columnList = "entity"),
})
@NoArgsConstructor
@AllArgsConstructor
public class EntityIndex extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //한국시간기준
    private LocalDate dt;

    private String entity;

    private Long max;
    private Long min;

}
