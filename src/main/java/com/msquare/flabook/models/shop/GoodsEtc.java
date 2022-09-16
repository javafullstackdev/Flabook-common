package com.msquare.flabook.models.shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Store;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class GoodsEtc implements Serializable {

    @Field(analyze = Analyze.NO, store = Store.YES)
    @Column(columnDefinition = "bit default 0")
    private boolean isBlind = false;

    @Field(analyze = Analyze.NO, store = Store.YES)
    @Column(columnDefinition = "bit default 0")
    private boolean isRecommended = false;

}
