package com.msquare.flabook.models;

import lombok.*;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(name = "UK_LIKE", columnNames = {"user_id", "comment_id"})})
public class ReportComment extends AbstractReport implements IUserCommentActivity {

    @NonNull
    @Embedded
    private UserCommentRelation relation;

}
