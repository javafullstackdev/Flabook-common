package com.msquare.flabook.models.board;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.models.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.search.annotations.Indexed;
import org.mapstruct.Named;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Indexed(index = ElasticsearchConfig.INDEX_NAME)
@DiscriminatorValue(BoardDiscriminatorValues.MAGAZINE)
@DynamicUpdate
@ToString(callSuper = true)
/*
 * 질문하기
 */
public class Magazine extends Posting implements INotification {

    @Override
    public PostingType getPostingType() {
        return PostingType.magazine;
    }

    @Override
    public List<PosterAttachment> getPosters() {
        return null; //NOSONAR
    }

    @Override
    public void setPosters(List<PosterAttachment> posters) {
        //
    }

    private String title;

    @Override
    @Named("checkQualifiedNamed")
    public Resource asResource() {
        return new Resource(getId(), Resource.ResourceType.magazine, getId(), Resource.ResourceType.magazine);
    }
}
