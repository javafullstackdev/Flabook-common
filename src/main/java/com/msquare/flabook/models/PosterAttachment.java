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
@DiscriminatorValue("T")
@SuperBuilder
@NoArgsConstructor
@ToString(exclude = {"parent"})
/**
 * 가이드북 포스터
 */
public class PosterAttachment extends Attachment {

    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT), nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    public Posting parent;

}
