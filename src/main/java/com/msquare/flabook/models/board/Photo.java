package com.msquare.flabook.models.board;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.models.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.search.annotations.Indexed;
import org.mapstruct.Named;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Indexed(index = ElasticsearchConfig.INDEX_NAME)
@DiscriminatorValue(BoardDiscriminatorValues.PHOTO)
@DynamicUpdate
@ToString(callSuper = true)
/*
 * 모야모 포토
 */
public class Photo extends Posting implements INotification{

    @Override
    public PostingType getPostingType() {
        return PostingType.photo;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void setTitle(String title) {
        //포토는 타이틀 사용안함
    }

    @Override
    public void setText(String text) {
        super.setText(Optional
            .ofNullable(text)
            .map(str -> str.replaceAll("\\R+", " "))
            .orElse(null));
    }

    @Override
    @Named("checkQualifiedNamed")
    public Resource asResource() {
        return new Resource(getId(), Resource.ResourceType.photo, getId(), Resource.ResourceType.photo);

    }

    @SuppressWarnings("java:S1168")
    @Override
    public List<PosterAttachment> getPosters() {
        return null;
    }

    @Override
    public void setPosters(List<PosterAttachment> posters) {
        //포토는 포스터 없음
    }

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhotoPhotoAlbum> photoAlbums = new ArrayList<>();

    @Override
    public List<PhotoPhotoAlbum> getPhotoAlbums() {
        return this.photoAlbums;
    }

    @Override
    public void setPhotoAlbums(List<PhotoPhotoAlbum> photoAlbums) {
        this.photoAlbums = photoAlbums;
    }

}
