package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.msquare.flabook.dto.mapper.CommentMapper;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.Comment;
import com.msquare.flabook.util.MentionUtils;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.BaseView.class)
@JsonIgnoreProperties({"delete", "mention", "blind"})
public class CommentDto implements Serializable {

    private long id;
    private String text;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties("parent")
    private List<AttachmentDto> attachments;

    @Accessors(chain = true)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String type;

    @Accessors(chain = true)
    @JsonIgnore
    private PostingType postingType;

    @Accessors(chain = true)
    @JsonIgnore
    private boolean isFirstComment = false;

    @Accessors(chain = true)
    @JsonIgnore
    private Long targetUserId;

    @JsonView(Views.WebAdminJsonView.class)
    public List<AttachmentDto> getOrgAttachments() {
        return this.attachments;
    }

    public List<AttachmentDto> getAttachments() {
        if (this.isBlind || this.isDelete /*|| (this.owner != null && this.owner.getRole().equals(UserRole.BAN))*/) {
            return Collections.emptyList();
        }
        return this.attachments;
    }

    public String getText() {
        if (this.isDelete ) {
            return Messages.DELETED_MESSAGE;
        } else if (this.isBlind ) {
            return Messages.BLIND_MESSAGE;
        }
        return MentionUtils.build(this.text, this.mentions);
    }

    private static final String EMPTY_STRING = "";

    public String getPlainText() {
        if(this.text == null)
            return EMPTY_STRING;

        if (this.isDelete ) {
            return Messages.DELETED_MESSAGE;
        } else if (this.isBlind ) {
            return Messages.BLIND_MESSAGE;
        }
        return this.text;
    }

    @JsonView(Views.WebAdminJsonView.class)
    public String getOrgText() {
        if(this.text == null)
            return EMPTY_STRING;

        return MentionUtils.build(this.text, this.mentions);
    }

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties({"parent", "mention", "blind", "delete"})
    private List<CommentDto> children;

    private UserDto owner;

    @JsonView(Views.WebAdminJsonView.class)
    private boolean isDelete;

    @JsonView(Views.WebAdminJsonView.class)
    private boolean isBlind;

    private boolean isAdopt;

    private Set<UserDto> mentions;

    @JsonProperty("mentions")
    @JsonIgnoreProperties({"levelInfo", "photoUrl", "role", "level", "status"})
    public Set<UserDto> getMentions() {
        if(mentions != null) {
            return this.mentions;
        }
        return null; //NOSONAR
    }

    private ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;

    public static CommentDto of(Comment comment) {
        return CommentMapper.INSTANCE.of(comment);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    //cacheevict 용
    @Accessors(chain = true)
    private Long postingId;

}
