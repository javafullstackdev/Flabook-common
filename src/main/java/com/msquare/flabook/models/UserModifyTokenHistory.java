package com.msquare.flabook.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.search.annotations.NumericField;
import org.hibernate.search.annotations.SortableField;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@DynamicUpdate
@JsonIgnoreProperties({"hibernateLazyInitializer"})
/*@Table(
	indexes = {@Index(name = "IDK_USER_ID", columnList = "user_id"),
        @Index(name = "IDK_ACCESS_TOKEN", columnList = "access_token")
    })*/
public class UserModifyTokenHistory extends BaseEntity {

    @Id
    @NumericField
    @SortableField
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String accessToken;
    private String refreshToken;
    private String event;
}
