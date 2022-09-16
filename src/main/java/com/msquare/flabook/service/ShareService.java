package com.msquare.flabook.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.dto.ShareDto;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.models.*;
import com.msquare.flabook.repository.*;
import com.msquare.flabook.service.elasticsearch.CacheValues;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.msquare.flabook.util.ValidateNotPhotoUtils.validateNotPhoto;

@Slf4j
@Service
@AllArgsConstructor
public class ShareService {

    private final TagRepository tagRepository;
    private final PostingRepository postingRepository;
    private final GambleRepository gambleRepository;
    private final ShareRepository shareRepository;
    private final DeeplinkService deeplinkService;
    //포토 - 사용자id를 기반 랜덤 사진 포스팅 공유 or photoAlbum 공유 or 사용자 id 공유
    private final UserRepository userRepository;

    @Transactional
    public ShareDto doSharePosting(User owner, Long id) {
        Posting posting = postingRepository.findById(id).orElseThrow(() -> new FlabookGlobalException("not found Entity"));
        validateNotPhoto(posting);
        if(posting.isBlind() || posting.isDelete()) {
            throw new FlabookGlobalException("공유할 수 없는 게시글 입니다.");
        }

        Resource resource = posting.asResource();
        Share share = shareRepository.findByResourceAndUser(resource, owner);
        if (share != null) {
            return ShareDto.of(share);
        }

        //공유ID
        UUID uuid = UUID.randomUUID();
        String deeplink = deeplinkService.getDynamicLink(posting, uuid.toString());
        share = new Share(null, uuid, resource, owner, deeplink);
        return ShareDto.of(shareRepository.save(share));
    }

    @Transactional
    public ShareDto doShareGamble(User owner, Long id) {
        Gamble gamble = gambleRepository.findById(id).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY));
        if(!gamble.isActive()) {
            throw new FlabookGlobalException(FlabookGlobalException.Messages.CAN_NOT_SHARE_EVENT);
        }

        Resource resource = gamble.asResource();
        Share share = shareRepository.findByResourceAndUser(resource, owner);
        if (share != null) {
            return ShareDto.of(share);
        }

        //공유ID
        UUID uuid = UUID.randomUUID();
        String deeplink = deeplinkService.getDynamicLink(gamble, uuid.toString());
        share = new Share(null, uuid, resource, owner, deeplink);
        return ShareDto.of(shareRepository.save(share));
    }

    @Transactional
    public ShareDto doShareDictionary(User owner, String name) {

        Optional<Tag> optionalTag = tagRepository.findByName(name);
        Tag tag = optionalTag.orElse(Tag.builder().name(name).originalName(name).tagType(Tag.TagType.dictionary).build());

        Resource resource = tag.asResource();
        Share share = shareRepository.findByResourceAndUser(resource, owner);
        if (share != null) {
            return ShareDto.of(share);
        }
        //공유ID
        UUID uuid = UUID.randomUUID();
        String deeplink = deeplinkService.getDynamicLink(tag, uuid.toString());
        share = new Share(null, uuid, resource, owner, deeplink);
        return ShareDto.of(shareRepository.save(share));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheValues.SHARES, key = "#id")
    public ShareDto findShare(UUID id) {
        return shareRepository.findByUUID(id).map(ShareDto::of).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY));
    }



    @Transactional
    public ShareDto doSharePhotoAlbum(User owner, Long userId) {

        //방법1
//        User user = userRepository.findById(userId).orElseThrow(() -> new FlabookGlobalException("not found User"));
//        Posting posting = null;
//        for( PhotoAlbum photoAlbum : user.getPhotoAlbums()){
//            if (photoAlbum.getPhotoCnt() > 0) {
//                posting = photoAlbum.getPhotos().get(0);
//                break;
//            }
//        }
//        if(Objects.isNull(posting)) throw new FlabookGlobalException("not found PhotoAlbum");
//
//        return doSharePosting(owner, posting.getId());

        //방법2
//        User user = userRepository.findById(userId).orElseThrow(() -> new FlabookGlobalException("not found User"));
//
//        if(user.getTotalPhotosCnt()==0) throw  new FlabookGlobalException("not found album");
//        PhotoAlbum photoAlbum = user.getPhotoAlbums().get(0);
//
//        Resource resource = new Resource(photoAlbum.getId(), Resource.ResourceType.photoalbum, photoAlbum.getId(), Resource.ResourceType.photoalbum);
//        Share share = shareRepository.findByResourceAndUser(resource, owner);
//        if (share != null) {
//            return ShareDto.of(share);
//        }
//
//        //공유ID
//        UUID uuid = UUID.randomUUID();
//        String deeplink = deeplinkService.getDynamicLink(photoAlbum, uuid.toString());
//        share = new Share(null, uuid, resource, owner, deeplink);
//        return ShareDto.of(shareRepository.save(share));

        //방법3
        User user = userRepository.findById(userId).orElseThrow(() -> new FlabookGlobalException("not found User"));

        if(user.getTotalPhotosCnt()==0) throw  new FlabookGlobalException("not found album");
        PhotoAlbum photoAlbum = user.getPhotoAlbums().get(user.getPhotoAlbums().size()-1);

        Resource resource = photoAlbum.asResource();
        Share share = shareRepository.findByResourceAndUser(resource, owner);
        if (share != null) {
            return ShareDto.of(share);
        }

        //공유ID
        UUID uuid = UUID.randomUUID();
        String deeplink = deeplinkService.getDynamicLink(photoAlbum, uuid.toString());
        share = new Share(null, uuid, resource, owner, deeplink);
        return ShareDto.of(shareRepository.save(share));
    }
}
