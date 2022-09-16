package com.msquare.flabook.service;

import lombok.RequiredArgsConstructor;
import com.msquare.flabook.dto.PhotoAlbumDto;
import com.msquare.flabook.dto.PostingDto;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.models.PhotoAlbum;
import com.msquare.flabook.models.PhotoPhotoAlbum;
import com.msquare.flabook.models.User;
import com.msquare.flabook.repository.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhotoAlbumService {

    private final PhotoAlbumRepository photoAlbumRepository;
    private final UserRepository userRepository;
    private final PhotoPhotoAlbumRepository photoPhotoAlbumRepository;
    private final PhotoPhotoAlbumRepositoryCustom photoPhotoAlbumRepositoryCustom;
    private final PostingRepository postingRepository;

    public void savePhotoAlbum(PhotoAlbum photoAlbum){
        photoAlbumRepository.save(photoAlbum);
    }

    @Transactional
    @Cacheable(value = "myPhotoAlbum", key = "#currentUser.id")
    public List<PhotoAlbumDto> getMyPhotoAlbum(User currentUser){
        getTheWholeAlbum(currentUser);
        return currentUser.getPhotoAlbums().stream().map(PhotoAlbumDto::of).collect(Collectors.toList());
    }

    @Transactional
    @Cacheable(value = "photoAlbum", key = "#userId")
    public List<PhotoAlbumDto> getPhotoAlbumByUserId(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new FlabookGlobalException( FlabookGlobalException.Messages.NOT_FOUND_ENTITY + userId));
        if(user.getPhotoAlbums().isEmpty() || user.getPhotoAlbums().stream().filter(photoAlbum -> photoAlbum.getName().equals("전체")).count()==0)
            getTheWholeAlbum(user);
        return user.getPhotoAlbums().stream().map(PhotoAlbumDto::of).collect(Collectors.toList());
    }

    @Transactional
    public List<PhotoAlbumDto> getPhotoAlbumByUserIdPage(long userId, String query, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(() -> new FlabookGlobalException( FlabookGlobalException.Messages.NOT_FOUND_ENTITY + userId));
        if(user.getPhotoAlbums().isEmpty() || user.getPhotoAlbums().stream().noneMatch(photoAlbum -> photoAlbum.getName().equals("전체")))
            getTheWholeAlbum(user);
        return photoAlbumRepository.findPhotoAlbumByUser(user, query, pageable).stream().map(PhotoAlbumDto::of).collect(Collectors.toList());
   }

    @Transactional
    public List<PhotoAlbumDto> getPhotoAlbumByUserIdContainsQuery(long userId, String query) {
        User user = userRepository.findById(userId).orElseThrow(() -> new FlabookGlobalException( FlabookGlobalException.Messages.NOT_FOUND_ENTITY + userId));
        if(user.getPhotoAlbums().isEmpty() || user.getPhotoAlbums().stream().noneMatch(photoAlbum -> photoAlbum.getName().equals("전체")))
            getTheWholeAlbum(user);
        return photoAlbumRepository.findPhotoAlbumByUser(user, query).stream().map(PhotoAlbumDto::of).collect(Collectors.toList());
    }

    /**
     * 앨범 생성, 수정, 삭제
     * */
    @Transactional
    @CacheEvict(value = "photoAlbum", key = "#currentUser.id")
    public PhotoAlbum createPhotoAlbum(User currentUser, String name){
        getTheWholeAlbum(currentUser);
        name = checkPhotoAlbumName(currentUser, name);
        return photoAlbumRepository.save(new PhotoAlbum(currentUser, name));
    }

    public String checkPhotoAlbumName(User currentUser, String name){
        name = name.replaceAll("(\r\n|\r|\n|\n\r)", " ");
        name = name.trim();

        if(name.isEmpty() || name.replace(" ", "").isEmpty()){
            throw new FlabookGlobalException("앨범명을 입력해주세요.");
        }

        List<PhotoAlbum> photoAlbumListByName = photoAlbumRepository.findPhotoAlbumListByName(currentUser, name);
        if (!photoAlbumListByName.isEmpty()) {
            throw new FlabookGlobalException("이미 존재하는 앨범명입니다.");
        }

        return name;
    }

    @Transactional
    @CacheEvict(value = "photoAlbum", key = "#currentUser.id")
    public void updatePhotoAlbum(User currentUser, long albumId, String name){
        PhotoAlbum photoAlbum = photoAlbumRepository.findById(albumId).orElse(null);
        if(Objects.isNull(photoAlbum)) throw new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY+albumId);
        if(!photoAlbum.getUser().equals(currentUser)) throw new FlabookGlobalException("수정 권한이 없습니다.");
        if(photoAlbum.getName().equals("전체")) throw new FlabookGlobalException("해당 앨범은 수정이 불가합니다");

        if(photoAlbum.getName().equals(name)){
            return;
        }

        photoAlbum.setName(checkPhotoAlbumName(currentUser, name));
        photoAlbumRepository.save(photoAlbum);
    }

    @Transactional
    @CacheEvict(value = "photoAlbum", key = "#currentUser.id")
    public void deletePhotoAlbum(User currentUser, long albumId){
        PhotoAlbum photoAlbum = photoAlbumRepository.findById(albumId).orElse(null);
        if(Objects.isNull(photoAlbum)) return;
        if(!photoAlbum.getUser().equals(currentUser)) throw new FlabookGlobalException("삭제 권한이 없습니다.");
        if(photoAlbum.getName().equals("전체")) throw new FlabookGlobalException("해당 앨범은 삭제가 불가합니다");

        for(PhotoPhotoAlbum photo : photoAlbum.getPhotos()){
            updateDecrementPhotoCnt(albumId);

            photoPhotoAlbumRepository.delete(photo);
        }
        photoAlbumRepository.delete(photoAlbum);

    }

    /**
     * "전체"앨범 찾기 or 없으면 만들기
     * */
    @Transactional
    public PhotoAlbum getTheWholeAlbum(User currentUser){
        PhotoAlbum album = photoAlbumRepository.findPhotoAlbumByName(currentUser, "전체").orElse(null);
        if(Objects.isNull(album)){
            album = photoAlbumRepository.save( new PhotoAlbum(currentUser, "전체"));
        }
        return album;
    }

    public PhotoAlbum getPhotoAlbumByName(User user, String albumName) {
        return photoAlbumRepository.findPhotoAlbumByName(user, albumName).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY + albumName));
    }

    public int updateIncrementPhotoCnt(Long id) {
        return photoAlbumRepository.updateIncrementPhotoCnt(id);
    }

    public int updateDecrementPhotoCnt(Long id) {
        return photoAlbumRepository.updateDecrementPhotoCnt(id);
    }


    /**
     * 마이페이지에서 사용할 대표사진
     * */

    @Transactional
    @Cacheable(value = "representPhotos", key = "#id")
    public List<PostingDto> findUsersRepresentPhotos(Long id) {
        if(userRepository.getOne(id).getTotalPhotosCnt()==0)
            return Collections.emptyList();
        Long photoAlbumId = getTheWholeAlbum(userRepository.getOne(id)).getId();
        return postingRepository.findRepresentPhotosByPhotoAlbumId(photoAlbumId).stream().map(PostingDto::of).collect(Collectors.toList());
    }

    /**
     * 작가별에서 사용할 대표사진
     * */
    @Transactional
    @Cacheable(value = "writerRepresentPhotos", key = "#id")
    public List<PostingDto> findWritersRepresentPhotos(Long id) {
        Long photoAlbumId = getTheWholeAlbum(userRepository.getOne(id)).getId();
        return postingRepository.findWriterRepresentPhotosByPhotoAlbumId(photoAlbumId).stream().map(PostingDto::of).collect(Collectors.toList());
    }


    /**
     * 앨범 상세
     * */

    @Transactional
//    @Cacheable(value = "detailPhotoAlbum", key = "{#albumId, #sinceId, #maxId, #count, #orderby, #offset}")
    public List<PostingDto> getPhotosByUserAndAlbum (long albumId, Long sinceId, Long maxId, int count, String orderby, int offset) {
        return photoPhotoAlbumRepositoryCustom.photoAlbumsPhotoList(albumId, sinceId, maxId, count, orderby, offset).stream().map(PostingDto::of).collect(Collectors.toList());
    }

    @Transactional
    public PhotoAlbum getPhotoAlbumById(Long photoAlbumId) {
        return photoAlbumRepository.findById(photoAlbumId).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY));
    }

}
