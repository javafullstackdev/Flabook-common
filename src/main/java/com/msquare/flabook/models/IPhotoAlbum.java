package com.msquare.flabook.models;

import java.util.ArrayList;
import java.util.List;

public interface IPhotoAlbum {
    default List<PhotoPhotoAlbum> getPhotoAlbums() {
        return new ArrayList<>();
    }

    default void setPhotoAlbums(List<PhotoPhotoAlbum> photoAlbums) {

    }

}
