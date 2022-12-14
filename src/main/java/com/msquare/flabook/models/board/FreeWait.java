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
@DiscriminatorValue(BoardDiscriminatorValues.FREE_WAIT)
@DynamicUpdate
@ToString(callSuper = true)
/*
 * μλνκΈ°
 */
public class FreeWait extends Posting implements INotification {

    @Override
    public PostingType getPostingType() {
        return PostingType.free_wait;
    }

    @Override
    @Named("checkQualifiedNamed")
    public Resource asResource() {
        return new Resource(getId(), Resource.ResourceType.free, getId(), Resource.ResourceType.free);
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void setTitle(String title) {
        //
    }

    @Override
    public List<PosterAttachment> getPosters() {
        return null; //NOSONAR
    }

    @Override
    public void setPosters(List<PosterAttachment> posters) {
        //
    }
}
