package com.msquare.flabook.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class UserCommentRelation implements Serializable {

    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "NONE"))
    @IndexedEmbedded(includePaths = {"id"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("posting")
    private User user;

    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "NONE"))
    @IndexedEmbedded(includePaths = {"id"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"children", "attachments", "parent"})
    private Comment comment;

}
