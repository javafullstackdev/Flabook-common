package com.msquare.flabook.models;

import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.UUID;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "UK_SHARE", columnNames = {"owner_id", "resourceId", "resourceType", "referenceId", "referenceType"})})
public class Share extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @NaturalId
    @Column(columnDefinition = "BINARY(16)")
    private UUID uuid;

    @AttributeOverride(name = "resourceId", column = @Column(name = "resourceId"))
    @AttributeOverride(name = "resourceType", column = @Column(name = "resourceType"))
    @AttributeOverride(name = "referenceId", column = @Column(name = "referenceId"))
    @AttributeOverride(name = "referenceType", column = @Column(name = "referenceType"))
    @Embedded
    private Resource resource;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    private String deeplink;

}
