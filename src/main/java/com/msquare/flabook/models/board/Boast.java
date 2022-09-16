package com.msquare.flabook.models.board;

import lombok.*;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.models.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.search.annotations.Indexed;
import org.mapstruct.Named;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Indexed(index = ElasticsearchConfig.INDEX_NAME)
@DiscriminatorValue(BoardDiscriminatorValues.BOAST)
@DynamicUpdate
@ToString(callSuper = true)
/*
 * 자랑하기
 */
public class Boast extends Posting implements INotification {

    @Override
    public PostingType getPostingType() {
        return PostingType.boast;
    }

    @NonNull
    private String title;

    @Override
    @Named("checkQualifiedNamed")
    public Resource asResource() {
        return new Resource(getId(), Resource.ResourceType.boast, getId(), Resource.ResourceType.boast);
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
