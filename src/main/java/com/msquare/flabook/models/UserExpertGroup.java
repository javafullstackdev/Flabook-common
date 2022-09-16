package com.msquare.flabook.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.msquare.flabook.enumeration.ExpertGroup;
import com.msquare.flabook.json.Views;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;

@EqualsAndHashCode(callSuper=false)
@Data
@Entity
@NoArgsConstructor
@DynamicUpdate
@ToString(exclude = {"user"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "user"})
public class UserExpertGroup extends BaseEntity {

    @Id
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;

    @JsonView(Views.WebAdminJsonView.class)
    @Field(index = org.hibernate.search.annotations.Index.YES, analyze = Analyze.NO, store = Store.YES)
    @Column(columnDefinition = "smallint default 0")
    @Enumerated(EnumType.ORDINAL)
    private ExpertGroup expertGroup;

}
