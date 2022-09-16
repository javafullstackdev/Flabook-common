package com.msquare.flabook.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.enumeration.RecommendKeywordType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendKeywordAdmin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(12)")
    @Enumerated(EnumType.STRING)
    private RecommendKeywordType type;

    private String content;

    @OneToMany(mappedBy = "recommendKeywordAdmin", cascade = {CascadeType.DETACH, CascadeType.MERGE})
    private List<RecommendKeyword> keywords = new ArrayList<>();



}
