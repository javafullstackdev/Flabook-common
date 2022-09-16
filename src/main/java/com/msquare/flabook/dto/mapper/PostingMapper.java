package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.ClinicConditionDto;
import com.msquare.flabook.dto.PhotoAlbumDto;
import com.msquare.flabook.dto.PostingDto;
import com.msquare.flabook.dto.UserDto;
import com.msquare.flabook.models.PhotoAlbum;
import com.msquare.flabook.models.Posting;
import com.msquare.flabook.models.User;
import com.msquare.flabook.models.board.ClinicCondition;
import com.msquare.flabook.models.board.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostingMapper {
    PostingMapper INSTANCE = Mappers.getMapper( PostingMapper.class );

    @Named("conditionOf")
    static ClinicConditionDto textOf(ClinicCondition clinicCondition) {
        return ClinicConditionDto.of(clinicCondition);
    }

    @Mapping(target = "condition", qualifiedByName = "conditionOf")
    @Mapping(target = "resource", qualifiedByName = "checkQualifiedNamed")
    PostingDto of(Posting posting);
    UserDto of(User user);


//    @Mapping(target = "photoAlbum", source = "posting", qualifiedByName = "setPhotoAlbum")
//    PostingDto ofPhoto(Posting posting);
//
//    @Named("setPhotoAlbum")
//    default PhotoAlbumDto setPhotoAlbum(Posting posting){
//        return PhotoAlbumDto.of(((Photo)posting).getPhotoAlbum());
//    }
}
