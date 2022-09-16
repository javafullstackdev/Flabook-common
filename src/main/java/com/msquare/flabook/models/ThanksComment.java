package com.msquare.flabook.models;

import lombok.*;
import com.msquare.flabook.enumeration.PostingType;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "THANKS_COMMENT", uniqueConstraints = {@UniqueConstraint(name="UK_THANKS", columnNames = {"source_comment_id", "user_id"})})
public class ThanksComment extends AbstractCommentActivity {

    @SuppressWarnings("unused")
    @Builder
    public ThanksComment(@NonNull UserPostingRelation relation, @NonNull UserCommentRelation source, @NonNull UserCommentRelation target) {
        super(relation, source, target);
    }

    @Override
    public String getTitle() {
        try {
            String nickname = getSource().getUser().getNickname();
            return String.format("%s님이 감사 인사 댓글을 남겼습니다.", nickname);
        } catch (Exception e) {
            return "감사 인사 댓글을 남겼습니다.";
        }
    }

    @Override
    public @NonNull PostingType getPostingType() {
        return PostingType.question;
    }
}
