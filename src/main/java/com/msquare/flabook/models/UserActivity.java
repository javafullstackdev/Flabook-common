package com.msquare.flabook.models;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import com.msquare.flabook.json.Views;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Store;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@DynamicUpdate
@JsonView(Views.UserProfileDetailJsonView.class)
public class UserActivity implements Serializable {

    @Field(index = org.hibernate.search.annotations.Index.YES, analyze = Analyze.NO, store = Store.YES)
    @Column(columnDefinition = "int default 0")
    private int postingCount = 0;

    @Field(index = org.hibernate.search.annotations.Index.YES, analyze = Analyze.NO, store = Store.YES)
    @Column(columnDefinition = "int default 0")
    private int commentCount = 0;

    @Field(index = org.hibernate.search.annotations.Index.YES, analyze = Analyze.NO, store = Store.YES)
    @Column(columnDefinition = "int default 0")
    private int adoptedCount = 0;

}
