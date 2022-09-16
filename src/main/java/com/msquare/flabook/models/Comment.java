package com.msquare.flabook.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.pattern.PatternReplaceCharFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Parameter;

import javax.persistence.*;
import java.util.List;


@Data
//@Indexed(index = ElasticsearchConfig.INDEX_NAME)
@Entity
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, of = {"id"})
@ToString
@Table(indexes = {@javax.persistence.Index(name = "IDX_POSTING", columnList = "posting_id")})
@JsonIgnoreProperties({"hibernateLazwyInitializer"})
@AnalyzerDef(name = "comment",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
                @TokenFilterDef(factory = StandardFilterFactory.class),
                /* 화이트스페이스 토크나이져일경우는 아래만 */
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = StopFilterFactory.class),
        }
)
@AnalyzerDef(name = "comment_remove_nickname_analyzer", charFilters =
        {
                @CharFilterDef(factory = PatternReplaceCharFilterFactory.class, params =
                        {
                                @Parameter(name = "pattern", value = "(\\[.*\\])"),
                                @Parameter(name = "replacement", value = "")
                        })
        }, tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters =
        {
                @TokenFilterDef(factory = StandardFilterFactory.class), @TokenFilterDef(factory = LowerCaseFilterFactory.class),
        })

public class Comment extends BaseEntity implements INotification, IActivity {

    public static final int MAX_TEXT_LENGTH = 2048;

    @Id
    @Access(AccessType.PROPERTY)
    @NumericField
    @SortableField
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * AnalyzerDefs 에 명시되지 않은 하단 @Field -> analyzer 의 definition 명은 elasticsearch 에 직접 세팅
     */
    @Column(length=MAX_TEXT_LENGTH, columnDefinition = "TEXT")
    @Field(name = "comment_text", index = Index.YES, analyzer = @Analyzer(definition = "comment"), store = Store.NO)
    //닉네임 패턴 [{닉네임}] 제거
    @Field(name = "comment_not_analyzed_text", index = org.hibernate.search.annotations.Index.YES,analyzer = @Analyzer(definition = "comment_remove_nickname_analyzer"), store = Store.YES)
    @Field(name = "comment_korean_text", index = Index.YES, analyzer = @Analyzer(definition = "comment_korean_text_analyzer"), store = Store.NO)
    private String text;

    @ToString.Exclude
    @JsonIgnore
    @ContainedIn
    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Posting posting;

    @ToString.Include
    @IndexedEmbedded(includeEmbeddedObjectId = true, includePaths = {"nickname"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("postings")
    private User owner;

    @ToString.Exclude
    @ContainedIn
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @ToString.Exclude
    @OrderColumn(name = "seq")
    @JoinTable(name = "Comment_User_Mention",
            inverseJoinColumns=@JoinColumn(name="user_id", columnDefinition="bigint comment '언급사용자ID'", referencedColumnName="id", foreignKey = @ForeignKey(name="none", value = ConstraintMode.NO_CONSTRAINT)),
            joinColumns=@JoinColumn(name="comment_id", columnDefinition = "bigint comment '답변ID'", referencedColumnName="id",  foreignKey = @ForeignKey(name="none", value = ConstraintMode.NO_CONSTRAINT))
    )
    @ManyToMany(fetch = FetchType.LAZY)
    private List<User> mentions;

    @org.hibernate.annotations.OrderBy(clause = "id asc")
    @ToString.Exclude
    @BatchSize(size = 50)
    @IndexedEmbedded(includeEmbeddedObjectId = true, includePaths = {"comment_text", "owner.id", "ownerId"})
    @JsonIgnoreProperties({"parent", "posting"})
    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Comment> children;

    @Where(clause = "dtype='C'")
    @ToString.Exclude
    @BatchSize(size = 50)
    //@JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "parent")
    @JsonIgnoreProperties("parent")
    private List<CommentAttachment> attachments;

    /**
     * @return 게시글의 이미지를 넘겨줌
     */
    @JsonIgnore
    @Override
    public ImageResource getThumbnail() {
        return (this.posting.getAttachments() != null && !this.posting.getAttachments().isEmpty()) ? this.posting.getAttachments().get(0).getImageResource() : null;
    }

    @Builder.Default
    @Field(store = Store.YES, analyze = Analyze.NO)
    @Column(columnDefinition = "bit default 0")
    private boolean isDelete = false;

    @Builder.Default
    @Field(store = Store.YES, analyze = Analyze.NO)
    @Column(columnDefinition = "bit default 0")
    private boolean isBlind = false;

    @Builder.Default
    @Column(columnDefinition = "int default 0")
    private int likeCount = 0;

    @Builder.Default
    @Column(columnDefinition = "int default 0")
    private int replyCount = 0;

    @Builder.Default
    @Column(columnDefinition = "int default 0")
    private int reportCount = 0;

    @Builder.Default
    @Column(columnDefinition = "bit default 0")
    private boolean isAdopt = false;

    /**
     * ownerId + isDelete
     * 내가쓴글 검색용
     * @return ownerId
     */
    @Field(store = Store.YES, analyze = Analyze.NO)
    public String getOwnerId() {
        if(this.getOwner() != null)
            return String.format("%s%b", this.getOwner().getId(), this.isDelete);
        return null;
    }

    @Override
    public Resource asResource() {
        if ((parent != null)) {
            Resource postingResource = getParent().getPosting().asResource();
            return new Resource(getId(), Resource.ResourceType.reply, getParent().getPosting().getId(), postingResource.getResourceType());
        } else {

            Resource postingResource = getPosting().asResource();
            return new Resource(getId(), Resource.ResourceType.comment, getPosting().getId(), postingResource.getResourceType());
        }
    }

}
