package com.msquare.flabook.models.board;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.models.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.Indexed;
import org.mapstruct.Named;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Indexed(index = ElasticsearchConfig.INDEX_NAME)
@DiscriminatorValue(BoardDiscriminatorValues.TELEVISION)
@DynamicUpdate
@ToString(callSuper = true)
/*
 * 모야모TV
 */
public class Television extends Posting implements INotification, IPoster<PosterAttachment> {

    @Override
    public PostingType getPostingType() {
        return PostingType.television;
    }

    @Override
    public List<PosterAttachment> getPosters() {
        return posters;
    }

    @Override
    public void setPosters(List<PosterAttachment> posters) {
        this.posters = posters;
    }

    private String title;

    @Column(columnDefinition = "varchar(32) comment '유튜브 동영상ID'")
    private String youtubeId;

    @OrderColumn(name = "seq")
    @ToString.Exclude
    @BatchSize(size = 50)
    @Where(clause = "dtype='T'")
    @org.hibernate.annotations.ForeignKey(name = "none")
    @JsonIgnoreProperties("parent")
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "parent")
    private List<PosterAttachment> posters;


    @Override
    @Named("checkQualifiedNamed")
    public Resource asResource() {
        return new Resource(getId(), Resource.ResourceType.television, getId(), Resource.ResourceType.television);
    }

    @Override
    public ImageResource getThumbnail() {
        return (this.getPosters() != null && !this.getPosters().isEmpty()) ? this.getPosters().get(0).getImageResource() : null;
    }
}
