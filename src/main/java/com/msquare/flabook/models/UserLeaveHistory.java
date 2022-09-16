package com.msquare.flabook.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@DynamicUpdate
@ToString(exclude = {"user"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "user"})
public class UserLeaveHistory extends BaseEntity {

    @Id
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;

}
