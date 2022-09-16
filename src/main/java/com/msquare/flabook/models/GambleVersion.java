package com.msquare.flabook.models;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GambleVersion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int version;

    private ZonedDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Gamble gamble;
}
