package com.msquare.flabook.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@DiscriminatorValue("C")
@ToString(exclude = {"parent"})
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
//@Table(indexes = {@Index(name = "IDX_PARENT", columnList = "parent_id")})
public class CommentAttachment extends Attachment {

    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT), nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    public Comment parent;

}
