package com.msquare.flabook.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.enumeration.RecommendKeywordType;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Builder
@Table(indexes = {@Index(name="IDX_TYPE", columnList = "type")})
public class RecommendKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;

    @Column(columnDefinition = "VARCHAR(12)")
    @Enumerated(EnumType.STRING)
    private RecommendKeywordType type;

    @Column(columnDefinition = "SMALLINT default 1")
    private int weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECOMMENDKEYWORDADMIN_ID")
    private RecommendKeywordAdmin recommendKeywordAdmin;


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(RecommendKeywordType type) {
        this.type = type;
    }

    private void setRecommendKeywordAdmin(RecommendKeywordAdmin recommendKeywordAdmin){
        recommendKeywordAdmin.getKeywords().add(this);
    }

    public RecommendKeyword(String name, RecommendKeywordType keywordType, int weight, RecommendKeywordAdmin recommendKeywordAdmin){
        this.name = name;
        this.type = keywordType;
        this.weight = weight;
        this.recommendKeywordAdmin = recommendKeywordAdmin;
        this.setRecommendKeywordAdmin(recommendKeywordAdmin);
    }

}
