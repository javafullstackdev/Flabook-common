package com.msquare.flabook.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.NumericField;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Indexed
public class PhotoRecentWriter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NumericField
    private Long id;

    @NonNull
    @Embedded
    private UserPostingRelation relation;

    private ZonedDateTime createdAt;

    public PhotoRecentWriter(@NonNull UserPostingRelation userPostingRelation, ZonedDateTime createdAt){
        this.relation = userPostingRelation;
        this.createdAt = createdAt;
    }
}
