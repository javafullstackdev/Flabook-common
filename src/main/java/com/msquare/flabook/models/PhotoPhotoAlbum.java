package com.msquare.flabook.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.msquare.flabook.models.board.Photo;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@ToString
@NoArgsConstructor
public class PhotoPhotoAlbum implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "PHOTO_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Photo photo;

    @JoinColumn(name = "PHOTOALBUM_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private PhotoAlbum photoAlbum;

    public PhotoPhotoAlbum(Photo photo, PhotoAlbum photoAlbum){
        this.photo = photo;
        this.photoAlbum = photoAlbum;
        this.setPhoto(photo);
        this.setPhotoAlbum(photoAlbum);
    }

    public void setPhoto(Photo photo){
        photo.getPhotoAlbums().add(this);
    }

    public void setPhotoAlbum(PhotoAlbum photoAlbum){
        photoAlbum.getPhotos().add(this);
    }
}
