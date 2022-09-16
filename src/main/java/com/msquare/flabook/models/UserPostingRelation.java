package com.msquare.flabook.models;

import java.io.Serializable;

import javax.persistence.ConstraintMode;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class UserPostingRelation implements Serializable {

    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "NONE"))
    @IndexedEmbedded(includePaths = {"id"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("posting")
    private User user;

    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "NONE"))
    @IndexedEmbedded(includePaths = {"id"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"comments", "attachments"})
    private Posting posting;

}
