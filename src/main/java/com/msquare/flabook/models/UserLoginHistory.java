package com.msquare.flabook.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.search.annotations.NumericField;
import org.hibernate.search.annotations.SortableField;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Entity
@NoArgsConstructor
@DynamicUpdate
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
/*@Table(
	indexes = {@Index(name = "IDK_USER_ID", columnList = "user_id"),
        @Index(name = "IDK_ACCESS_TOKEN", columnList = "access_token")
    })*/
public class UserLoginHistory implements Serializable {

    @Id
    @NumericField
    @SortableField
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String accessToken;

    private ZonedDateTime createdAt;
}
