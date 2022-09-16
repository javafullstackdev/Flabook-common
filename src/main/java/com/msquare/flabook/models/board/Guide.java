package com.msquare.flabook.models.board;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.models.*;
import org.apache.shiro.util.CollectionUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.Indexed;
import org.mapstruct.Named;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Indexed(index = ElasticsearchConfig.INDEX_NAME)
@DiscriminatorValue(BoardDiscriminatorValues.GUIDE)
@DynamicUpdate
@ToString(callSuper = true)
/*
 * 자랑하기
 */
public class Guide extends Posting implements INotification, IPoster<PosterAttachment> {

    @Override
    public PostingType getPostingType() {
        return PostingType.guidebook;
    }
    
    @Override
    public String getTitle() {
        return null; //NOSONAR
    }

    @Override
    public void setTitle(String title) {
        //
    }

    @OrderColumn(name = "seq")
    @ToString.Exclude
    @BatchSize(size = 50)
    @Where(clause = "dtype='T'")
    @org.hibernate.annotations.ForeignKey(name = "none")
    @JsonIgnoreProperties("parent")
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "parent")
    private List<PosterAttachment> posters;

    @Override
    public ImageResource getThumbnail() {
        return (!CollectionUtils.isEmpty(this.posters)) ? this.posters.get(0).getImageResource() : null;
    }

    @Override
    @Named("checkQualifiedNamed")
    public Resource asResource() {
        return new Resource(getId(), Resource.ResourceType.guidebook, getId(), Resource.ResourceType.guidebook);
    }
}
