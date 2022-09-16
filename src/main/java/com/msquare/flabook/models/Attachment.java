package com.msquare.flabook.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vividsolutions.jts.geom.Point;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.search.annotations.*;
import org.hibernate.search.spatial.Coordinates;

import javax.persistence.Index;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Data
@Entity
@DiscriminatorColumn(columnDefinition = "char(1)", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@ToString(exclude = {"location", "imageResource"}, callSuper = true)
@SuperBuilder
@DynamicUpdate
@Table(indexes = {@Index(name = "IDX_TYPE", columnList = "dtype"), @Index(name = "IDX_PARENT", columnList = "parent_id")})
public class Attachment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(insertable = false, updatable = false)
    private String dtype;


    @JsonIgnore
    private Point location;

    @Field(name = "attachment_korean_description", index = org.hibernate.search.annotations.Index.YES, store = Store.YES, analyzer = @Analyzer(definition = "attachment_korean_text_analyzer"))
    @Column(length=2048, columnDefinition = "TEXT default null")
    private String description;

    @JsonIgnore
    @Embedded
    @AttributeOverride(name = "dimension", column = @Column(name = "dimension"))
    //@AssociationOverride(name = "lines", joinTable = @JoinTable(name = "Person_HomeAddress_Line"))
    private ImageResource imageResource;

    @Column(columnDefinition = "varchar(11)")
    private String dimension;

    @JoinTable(name = "Attachment_Tag",
            joinColumns=@JoinColumn(name="attachment_id", referencedColumnName="id", foreignKey = @ForeignKey(name="none", value = ConstraintMode.NO_CONSTRAINT)),
            inverseJoinColumns=@JoinColumn(name="tag_id", referencedColumnName="id", foreignKey = @ForeignKey(name="none", value = ConstraintMode.NO_CONSTRAINT))
            ,uniqueConstraints = {@UniqueConstraint(name = "UK_ATTACHMENT_TAG", columnNames = {"attachment_id", "tag_id"})}
    )
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Tag> tags;

    @JsonIgnore
    @Spatial(spatialMode = SpatialMode.RANGE, store = Store.YES)
    public Coordinates getLocation() {
        if (location == null)
            return null;

        return new Coordinates() {
            @Override
            public Double getLatitude() {
                return location.getY();
            }

            @Override
            public Double getLongitude() {
                return location.getX();
            }
        };
    }

}
