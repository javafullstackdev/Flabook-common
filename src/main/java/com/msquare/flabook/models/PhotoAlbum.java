package com.msquare.flabook.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.mapstruct.Named;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "UK_PHOTOALBUM", columnNames = {"name", "user_id"})})
public class PhotoAlbum extends BaseEntity implements INotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    private String name;

    @Column(columnDefinition = "bit default 0")
    private Boolean isDelete = false;

    @OneToMany(mappedBy = "photoAlbum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhotoPhotoAlbum> photos = new ArrayList<>();

    private int photoCnt = 0;

    public PhotoAlbum(User user, String name) {
        this.user = user;
        this.name = name;
        this.setUserToPhotoAlbum(user);
    }

    public void setUserToPhotoAlbum(User user){
        user.getPhotoAlbums().add(this);
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public User getOwner() {
        return this.user;
    }

    @Override
    public ImageResource getThumbnail() {
        return null;
    }

    @Override
    @Named("checkQualifiedNamed")
    public Resource asResource() {
        return new Resource(user.getId(), Resource.ResourceType.photoalbum, user.getId(), Resource.ResourceType.photoalbum);
    }
}
