package com.msquare.flabook.models;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import com.msquare.flabook.enumeration.PostingType;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(name = "UK_LIKE", columnNames = {"user_id", "posting_id"})})
public class ReportPosting extends AbstractReport implements IUserPostingActivity {

    @NonNull
    @Column(columnDefinition = "tinyint")
    @Enumerated(EnumType.ORDINAL)
    private PostingType postingType;

    @NonNull
    @Embedded
    private UserPostingRelation relation;



}
