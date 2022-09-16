package com.msquare.flabook.models;

import lombok.*;
import com.msquare.flabook.enumeration.PostingType;
import org.hibernate.search.annotations.*;

import javax.persistence.*;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Indexed(index = ElasticsearchConfig.INDEX_NAME)
@Table(uniqueConstraints = {@UniqueConstraint(name = "UK_ADOPT", columnNames = {"user_id", "posting_id"})})
public class AdoptComment extends AbstractCommentActivity {

    @Builder
    public AdoptComment(@NonNull UserPostingRelation relation, @NonNull UserCommentRelation source, @NonNull UserCommentRelation target) {
        super(relation, source, target);
    }

    @Override
    public String getTitle() {
        try {
            String nickname = getSource().getUser().getNickname();
            return String.format("%s님이 당신의 답변을 채택하였습니다.", nickname);
        } catch (Exception e) {
            return "답변이 채택되었습니다.";
        }
    }

    @Override
    public @NonNull PostingType getPostingType() {
        return PostingType.clinic;
    }

    @DocumentId
    @NumericField
    @SortableField
    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    @IndexedEmbedded(includeEmbeddedObjectId = true, includePaths = {"user.id", "posting.title", "posting.posting_text", "posting.comments.comment_not_analyzed_text"})
    public @NonNull UserPostingRelation getRelation() {
        return super.getRelation();
    }
}
