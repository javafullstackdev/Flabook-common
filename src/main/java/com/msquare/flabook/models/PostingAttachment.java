package com.msquare.flabook.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("P")
@SuperBuilder
@NoArgsConstructor
@ToString(exclude = {"parent"})
public class PostingAttachment extends Attachment {

    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT), nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    public Posting parent;

    @Column(columnDefinition = "varchar(20) comment '장소명'")
    public String placeName;
}
