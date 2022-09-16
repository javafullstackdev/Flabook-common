package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.PhotoAlbumDto;
import com.msquare.flabook.dto.PostingDto;
import com.msquare.flabook.models.PhotoAlbum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PhotoAlbumMapper {

    PhotoAlbumMapper INSTANCE = Mappers.getMapper(PhotoAlbumMapper.class);

//    @Mapping(target = "ownerId", source = "photoAlbum", qualifiedByName = "setOwnerId")
    @Mapping(target = "representPhoto", source = "photoAlbum", qualifiedByName = "setRepresentPhoto")
    @Mapping(target = "photoCnt", source = "photoAlbum", qualifiedByName = "setPhotoCnt")
    PhotoAlbumDto of(PhotoAlbum photoAlbum);

    @Named("setPhotoCnt")
    default int setPhotoCnt(PhotoAlbum photoAlbum){
        return photoAlbum.getPhotos().size();
    }

    @Named("setRepresentPhoto")
    default PostingDto setRepresentPhoto(PhotoAlbum photoAlbum){
        if(photoAlbum.getPhotoCnt()==0)
            return null;
        return PostingDto.of(photoAlbum.getPhotos().get(photoAlbum.getPhotoCnt()-1).getPhoto());
    }

//    @Named("setPhotoCnt")
//    default long setOwnerId(PhotoAlbum photoAlbum){
//        return photoAlbum.getUser().getId();
//    }

}
