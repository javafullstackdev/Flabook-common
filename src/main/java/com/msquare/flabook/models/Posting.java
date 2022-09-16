package com.msquare.flabook.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.board.WithCondition;
import com.msquare.flabook.models.shop.Goods;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.*;

import javax.persistence.Index;
import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@DiscriminatorColumn(columnDefinition = "char(2)", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("PO")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@AllArgsConstructor
@ToString()
@Where(clause = "is_delete = false")
@DynamicUpdate
@AnalyzerDef(name = "text_analyzer",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
                @TokenFilterDef(factory = StandardFilterFactory.class),
                //화이트스페이스 토크나이져일경우는 아래만
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = StopFilterFactory.class),
        }
)
@Table(indexes = {@Index(name = "IDX_TYPE", columnList = "dtype")})
public abstract class Posting extends BaseEntity implements INotification, WithCondition, IActivity, IYoutube, IPhotoAlbum {

    @ToString.Include
    @Id
    @NumericField
    @SortableField
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    @Column(length=4096, columnDefinition = "TEXT")
    @Field(name = "posting_text", index = org.hibernate.search.annotations.Index.YES, store = Store.YES, analyzer = @Analyzer(definition = "text_analyzer"))
    @Field(name = "posting_korean_text", index = org.hibernate.search.annotations.Index.YES, store = Store.YES, analyzer = @Analyzer(definition = "posting_korean_text_analyzer"))
    private String text;

    @OrderColumn(name = "seq")
    @ToString.Exclude
    @BatchSize(size = 50)
    @Where(clause = "dtype='P'")
    @org.hibernate.annotations.ForeignKey(name = "none")
    @JsonIgnoreProperties("parent")
    @IndexedEmbedded(includeEmbeddedObjectId = true, includePaths = {"attachment_korean_description"})
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "parent")
    private List<PostingAttachment> attachments;

    @ToString.Exclude
    @Where(clause = "parent_id is null")
    @BatchSize(size = 50)
    @JsonIgnore
    @IndexedEmbedded(includeEmbeddedObjectId = true, includePaths = {"comment_text", "children.comment_text", "owner.id", "children.owner.id", "ownerId", "children.ownerId", "comment_not_analyzed_text", "children.comment_not_analyzed_text"})
    //@JoinColumn(name = "posting_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "posting")
    @JsonIgnoreProperties("posting")
    private List<Comment> comments;

    @Field(store = Store.YES, analyze = Analyze.NO)
    @Column(columnDefinition = "int default 0")
    private int readCount = 0;

    @Field(store = Store.YES, analyze = Analyze.NO)
    @Column(columnDefinition = "int default 0")
    private int commentCount = 0;

    @JsonView(Views.PostingActivityDetailJsonView.class)
    @Field(store = Store.YES, analyze = Analyze.NO)
    @Column(columnDefinition = "int default 0")
    private int receivingCount = 0;

    @JsonView(Views.PostingActivityDetailJsonView.class)
    @Field(store = Store.YES, analyze = Analyze.NO)
    @Column(columnDefinition = "int default 0")
    private int reportCount = 0;

    @JsonView(Views.PostingActivityDetailJsonView.class)
    @Field(store = Store.YES, analyze = Analyze.NO)
    @Column(columnDefinition = "int default 0")
    private int likeCount = 0;

    @JsonView(Views.PostingActivityDetailJsonView.class)
    @Field(store = Store.YES, analyze = Analyze.NO)
    @Column(columnDefinition = "int default 0")
    private int scrapCount = 0;

    @JsonView(Views.PostingActivityDetailJsonView.class)
    @Field(store = Store.YES, analyze = Analyze.NO)
    @Column(columnDefinition = "int default 0")
    private int shareCount = 0;


    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @IndexedEmbedded(includeEmbeddedObjectId = true, includePaths = {"level", "activity.postingCount", "activity.commentCount", "createdAt"})
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @Field(store = Store.YES, analyze = Analyze.NO)
    @Column(columnDefinition = "bit default 0")
    private boolean isDelete = false;

    @Field(store = Store.YES, analyze = Analyze.NO)
    @Column(columnDefinition = "bit default 0")
    private boolean isBlind = false;

    @Field(store = Store.YES, analyze = Analyze.NO)
    @Column(columnDefinition = "bit default 0")
    private boolean isAdopt = false;

    @JsonIgnore
    @Override
    public ImageResource getThumbnail() {
        return (this.getAttachments().size() > 0) ? this.getAttachments().get(0).getImageResource() : null;
    }

    @Override
    public User getOwner() {
        return this.owner;
    }

    public abstract PostingType getPostingType();

    @Field(store = Store.YES, analyze = Analyze.NO)
    @Field(name = "title_not_analyzed_text", index = org.hibernate.search.annotations.Index.YES, store = Store.YES, analyzer = @Analyzer(definition = "text_analyzer"))
    public abstract String getTitle();

    public abstract void setTitle(String title);
    public abstract List<PosterAttachment> getPosters();
    public abstract void setPosters(List<PosterAttachment> posters);


    @OrderColumn(name = "seq")
    @JoinTable(name = "Posting_Goods",
            inverseJoinColumns=@JoinColumn(name="goods_no", columnDefinition="varchar(12) comment '고도몰 상품ID'", referencedColumnName="id", foreignKey = @ForeignKey(name="none", value = ConstraintMode.NO_CONSTRAINT)),
            joinColumns=@JoinColumn(name="posting_id", columnDefinition = "bigint comment '게시글ID'", referencedColumnName="id",  foreignKey = @ForeignKey(name="none", value = ConstraintMode.NO_CONSTRAINT))
    )
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Goods> goodses;

    @Field(store = Store.YES, analyze = Analyze.YES)
    @DateBridge(resolution = Resolution.SECOND)
    @Override
    public ZonedDateTime getCreatedAt() {
        return super.getCreatedAt();
    }

    @Field(store = Store.YES, analyze = Analyze.YES)
    @DateBridge(resolution = Resolution.SECOND)
    @Override
    public ZonedDateTime getModifiedAt() {
        return super.getModifiedAt();
    }

    @IndexedEmbedded(includeEmbeddedObjectId = true, includePaths = {"name", "originalName"})
    @JoinTable(name = "Posting_Tag",
            inverseJoinColumns=@JoinColumn(name="tag_id", referencedColumnName="id", foreignKey = @ForeignKey(name="none", value = ConstraintMode.NO_CONSTRAINT)),
            joinColumns=@JoinColumn(name="posting_id", referencedColumnName="id", foreignKey = @ForeignKey(name="none", value = ConstraintMode.NO_CONSTRAINT))
            ,uniqueConstraints = {@UniqueConstraint(name = "UK_POSTING_TAG", columnNames = {"posting_id", "tag_id"})}
    )
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Tag> tags;
}
