package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.mapper.PostingMapper;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.IUserPostingActivity;
import com.msquare.flabook.models.PhotoPhotoAlbum;
import com.msquare.flabook.models.Posting;
import com.msquare.flabook.models.Resource;
import org.apache.commons.lang3.StringUtils;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@JsonView(Views.BaseView.class)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true, value = {"delete", "blind"})
public class PostingDto extends PostingActivityDto {

    private static final long serialVersionUID = 1L;

    private long id;

    private String title;
    private String text;

    public String getText() {
        if (this.isDelete) {
            return Messages.DELETED_MESSAGE;
        } else if (this.isBlind) {
            return Messages.BLIND_MESSAGE;
        }
        return this.text;
    }

    @JsonView(Views.WebAdminJsonView.class)
    public String getOrgText() {
    	return this.text;
    }

    private int readCount;
    private int commentCount;
    private int likeCount;
    private int scrapCount;

    @JsonView(Views.WebAdminJsonView.class)
    private int reportCount;

    @JsonView({Views.PostingActivityDetailJsonView.class, Views.WebAdminJsonView.class})
    private List<GoodsDto> goodses;

    private Resource resource;

    @JsonIgnoreProperties("parent")
    private List<AttachmentDto> attachments;

    @JsonIgnoreProperties("parent")
    private List<AttachmentDto> posters;

    public List<AttachmentDto> getAttachments() {
        if (this.isBlind || this.isDelete /*|| (this.owner != null && this.owner.getRole().equals(UserRole.BAN))*/) {
            return Collections.emptyList();
        }
        return this.attachments;
    }

    @JsonView(Views.WebAdminJsonView.class)
    public List<AttachmentDto> getOrgAttachments() {
    	return this.attachments;
    }

    public List<AttachmentDto> getPosters() {
        if (this.isBlind || this.isDelete /*|| (this.owner != null && this.owner.getRole().equals(UserRole.BAN))*/) {
            return Collections.emptyList();
        }
        return this.posters;
    }

    @JsonView(Views.WebAdminJsonView.class)
    public List<AttachmentDto> getOrgPosters() {
    	return this.posters;
    }

    private UserDto owner;

    @JsonView(Views.WebAdminJsonView.class)
    private boolean isDelete;

    @JsonView(Views.WebAdminJsonView.class)
    private boolean isBlind;

    @JsonView({Views.PostingActivityDetailJsonView.class, Views.PostingUserActivityJsonView.class})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    //@JsonView(Views.PostingLikeOnlyJsonView.class)
    @JsonProperty("isLike")
    private Boolean isLike;

    private boolean isAdopt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AnswerDto answer;

    private PostingType postingType;

    private ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ClinicConditionDto condition;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long timelineId;

    public static PostingDto of(Posting posting) {
        return PostingMapper.INSTANCE.of(posting);
    }

    public static PostingDto of(IUserPostingActivity activity) {
        PostingDto postingDto = PostingMapper.INSTANCE.of(activity.getRelation().getPosting());
        postingDto.setTimelineId(activity.getId());
        return postingDto;
    }

    public static PostingDto of(PhotoPhotoAlbum photoPhotoAlbum) {
        PostingDto postingDto = PostingMapper.INSTANCE.of(photoPhotoAlbum.getPhoto());
        postingDto.setTimelineId(photoPhotoAlbum.getId());
        return postingDto;
    }

    private String youtubeId;
    private String youtubeUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getYoutubeUrl() {
        if(StringUtils.isNotBlank(this.youtubeUrl)) {
            return this.youtubeUrl;
        }

        if(this.youtubeId == null) {
            return null;
        }

        return String.format("https://www.youtube.com/watch?v=%s", this.youtubeId);
    }

}
