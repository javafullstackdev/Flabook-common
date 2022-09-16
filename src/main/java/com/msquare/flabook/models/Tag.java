package com.msquare.flabook.models;


import com.vividsolutions.jts.geom.Geometry;
import lombok.*;
import com.msquare.flabook.common.configurations.ServiceHost;
import com.msquare.flabook.util.HangleUtil;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.ngram.NGramFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.*;

import javax.persistence.*;

/*
 * 자동완성 검색 및 도감
 */
@Indexed(index = ElasticsearchConfig.INDEX_NAME)
@AnalyzerDef(name = "ngram",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
                @TokenFilterDef(factory = StandardFilterFactory.class),
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = StopFilterFactory.class),
                @TokenFilterDef(factory = NGramFilterFactory.class,
                        params = {
                                @Parameter(name = "minGramSize", value = "3"),
                                @Parameter(name = "maxGramSize", value = "5")
                        })
        }
)
@SuppressWarnings("java:S115")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(of = {"id", "name", "originalName"})
@Table(uniqueConstraints = {@UniqueConstraint(name = "UK_TAGNAME", columnNames = {"name"})})
public class Tag extends BaseEntity implements INotification {

    //검색가능, 검색불가, 검색/입력불가
    public enum Visibility {visible, hidden, block}
    public enum SearchContentType {search, web, detail, profile, list, exam_list, exam}
    public enum TagType {none, dictionary, location}

    @Id
    @NumericField
    @SortableField
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field(store = Store.YES, analyze = Analyze.NO)
    @Column(unique = true, length = 100)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.nameAlphabet = HangleUtil.hangulToAlphabet(name);
        this.nameLowercase = name.toLowerCase();
        this.name = name;
    }

    /**
     * 자동완성 한글 자소분리
     */
    @Field(index = Index.YES, analyzer = @Analyzer(definition = "ngram"), store = Store.YES)
    private String nameAlphabet;

    @Field(store = Store.YES, analyze = Analyze.NO)
    private String nameLowercase;

    @Field(index = Index.YES, store = Store.YES)
    private Long plantId;

    @Field(index = Index.YES, store = Store.YES)
    @Column(length = 100)
    private String originalName;

    @Builder.Default
    @Field(index = Index.YES, store = Store.YES)
    @Column(columnDefinition = "smallint comment '0 : visible, 1 : hidden, 2 : block'")
    private Visibility visibility = Visibility.visible;

    @Builder.Default
    @Field(index = Index.YES, store = Store.YES)
    @Column(columnDefinition = "smallint comment '0 : none, 1 : dictionary, 2 : location'")
    private TagType tagType = TagType.none;

    //@Type(type="org.hibernate.spatial.GeometryType")
    private Geometry geometry;

    private String url;

    @Override
    public String getText() {
        return String.format("'%s' 도감으로 바로가기", this.name);
    }

    @Override
    public User getOwner() {
        return null;
    }

    @Override
    public ImageResource getThumbnail() {
        return new ImageResource(ServiceHost.getS3Url("commons/logo.png"), "");
    }

    @Override
    public Resource asResource() {
        return new Resource(this.name, Resource.ResourceType.dictionary, this.name, Resource.ResourceType.dictionary);
    }
}
