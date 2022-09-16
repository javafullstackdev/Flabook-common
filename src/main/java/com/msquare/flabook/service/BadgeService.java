package com.msquare.flabook.service;

import com.drew.imaging.ImageProcessingException;
import lombok.RequiredArgsConstructor;
import com.msquare.flabook.dto.BadgeAfterDto;
import com.msquare.flabook.dto.BadgeDto;
import com.msquare.flabook.dto.UserWithBadgeDto;
import com.msquare.flabook.dto.mapper.BadgeMapper;
import com.msquare.flabook.models.Badge;
import com.msquare.flabook.models.User;
import com.msquare.flabook.models.UserBadge;
import com.msquare.flabook.repository.BadgeRepository;
import com.msquare.flabook.repository.UserBadgeRepository;
import com.msquare.flabook.repository.UserRepository;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserBadgeService userBadgeService;
    private final NotificationService notificationService;
    private final ImageUploadService imageUploadService;
    /**
     * Badge CRUD
     */
    @Transactional
    public void createBadge(String title, String description1, String description2, MultipartFile trueImage, MultipartFile falseImage) {

        Badge badge = new Badge();
        badge.setTitle(title);
        badge.setDescription1(description1);
        badge.setDescription2(description2);

        try {
            ImageUploadService.ImageResourceInfoWithMetadata trueImageResourceInfoWithMetadata = imageUploadService.uploadWithMeta(FolderDatePatterns.BADGES, trueImage);
            badge.setTrueImageResource( trueImageResourceInfoWithMetadata.getImageResource() );

            ImageUploadService.ImageResourceInfoWithMetadata falseImageResourceInfoWithMetadata = imageUploadService.uploadWithMeta(FolderDatePatterns.BADGES, falseImage);
            badge.setFalseImageResource( falseImageResourceInfoWithMetadata.getImageResource() );

        } catch (IOException | ImageProcessingException e) {
            e.printStackTrace();
            throw new CompletionException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
            throw new CompletionException(e);
        }


        badgeRepository.save(badge);


    }

    @Transactional
    public void updateBadge(long id, String title, String description1, String description2){
        Badge badge = badgeRepository.findById(id).orElse(null);
        if(Objects.isNull(badge)) return;
        badge.setTitle(title);
        badge.setDescription1(description1);
        badge.setDescription2(description2);
    }

    @Transactional(readOnly = true)
    public BadgeDto getBadge(long id){
        Badge badge = badgeRepository.findById(id).orElse(null);
        if(Objects.isNull(badge)) return null;
        return BadgeMapper.INSTANCE.ofFalse(badge);
    }

    @Transactional
    public void deleteBadge(long id){
        Badge badge = badgeRepository.findById(id).orElse(null);
        if(Objects.isNull(badge)) return;
        badgeRepository.delete(badge);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "allBadges", key = "#userId")
    public UserWithBadgeDto getUserBadgeDto(long userId) {

        User user = userRepository.findById(userId).orElse(null);
        if(Objects.isNull(user)) return null;

        UserWithBadgeDto userWithBadgeDto = new UserWithBadgeDto();

        List<BadgeDto> allBadgeList = new ArrayList<>();
        List<Badge> findAllBadges = badgeRepository.findAll();
        List<Badge> myBadgeList = user.getMyBadges().stream().map(UserBadge::getBadge).collect(Collectors.toList());

        if(Objects.nonNull( user.getRepresentBadge() ) && BooleanUtils.isTrue(user.getRepresentBadge().getActive()) ) {
            Badge representBadge = user.getRepresentBadge();
            myBadgeList.removeIf(badge -> badge.getTitle().equals(representBadge.getTitle()));
            findAllBadges.removeIf(badge -> badge.getTitle().equals(representBadge.getTitle()));
            BadgeDto badgeDto = BadgeMapper.INSTANCE.ofTrue(representBadge);
            badgeDto.setCurrentRepresent(true);
            allBadgeList.add(badgeDto);
        }

        for(Badge badge : myBadgeList) {
            if(BooleanUtils.isNotTrue(badge.getActive())) continue;
            BadgeDto badgeDto = BadgeMapper.INSTANCE.ofTrue(badge);
            badgeDto.setCurrentRepresent(false);
            allBadgeList.add(badgeDto);
            findAllBadges.removeIf( badgeOne -> badgeOne.getTitle().equals(badge.getTitle()));
        }
        for(Badge badge : findAllBadges) {
            if(BooleanUtils.isNotTrue(badge.getActive())) continue;
            BadgeDto badgeDto = BadgeMapper.INSTANCE.ofFalse(badge);
            badgeDto.setCurrentRepresent(false);
            allBadgeList.add(badgeDto);
        }
        allBadgeList.sort(Comparator.comparingInt(BadgeDto::getOrderCount));

        userWithBadgeDto.setAllBadges(allBadgeList);

        return userWithBadgeDto;
    }

    @Transactional
    @CacheEvict(value = "allBadges", key = "#currentUser.id", condition = "#currentUser.id != null")
    public boolean setRepresentBadge(User currentUser, long id) {
        Badge representBadge = badgeRepository.findById(id).orElse(null);
        if(Objects.isNull(representBadge) || BooleanUtils.isNotTrue(representBadge.getActive()))
            return false;

        List<Badge> myBadges = currentUser.getMyBadges().stream().map(UserBadge::getBadge).collect(Collectors.toList());
        boolean matchCheck = myBadges.stream().anyMatch(badge -> badge.getTitle().equals(representBadge.getTitle()));
        if( !matchCheck )
            return false;

        currentUser.setRepresentBadge(representBadge);
        userRepository.save(currentUser);
        return true;
    }

    @Transactional
    @CacheEvict(value = "allBadges", key = "#currentUser.id", condition = "#currentUser.id != null")
    public void deleteUserRepresentBadge(User currentUser) {
        currentUser.setRepresentBadge(null);
        userRepository.save(currentUser);
    }


    /**
     * userbadge c,d
     * */
    @Transactional
    public void addUserBadges(long userId, long badgeId) {

        User user = userRepository.findById(userId).orElse(null);
        Badge badge = badgeRepository.findById(badgeId).orElse(null);
        if(Objects.isNull(user) || Objects.isNull(badge)) return;

        if(userBadgeService.isPresentUserBadge(user, badge)){
            return;
        }

        UserBadge userBadge = new UserBadge(user, badge);
        userBadge = userBadgeRepository.save(userBadge);

        notificationService.afterNewBadge(new BadgeAfterDto(Collections.singletonList(userBadge)));
        user.setBadgeCount(user.getMyBadges().size());

        userRepository.save(user);
    }

    @Transactional
    public void addUserBadges(long userId, Set<Long> badgeIds) {

        User user = userRepository.findById(userId).orElse(null);
        if(Objects.isNull(user) || CollectionUtils.isEmpty(badgeIds)) return;

        List<UserBadge> newUserBadges = new ArrayList<>();
        for(Long badgeId : badgeIds) {
            Badge badge = badgeRepository.getOne(badgeId);
            if(userBadgeService.isPresentUserBadge(user, badge)) {
                continue;
            }

            UserBadge userBadge = new UserBadge(user, badge);
            newUserBadges.add(userBadgeRepository.save(userBadge));
        }

        if(newUserBadges.isEmpty()) {
            return;
        }

        notificationService.afterNewBadge(new BadgeAfterDto(newUserBadges));
        user.setBadgeCount(user.getMyBadges().size());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUserBadges(User currentUser, long id) {
        Badge badge = badgeRepository.findById(id).orElse(null);
        if(Objects.isNull(badge)) return;

        if(userBadgeService.isPresentUserBadge(currentUser, badge)){
            userBadgeRepository.delete(userBadgeRepository.getOne(id));
        }

        currentUser.setBadgeCount(currentUser.getMyBadges().size());
        userRepository.save(currentUser);
    }

    @Transactional
    public void clear(User currentUser){
        currentUser.setBadgeCount(0);
        for(UserBadge ub : currentUser.getMyBadges()){
            userBadgeRepository.delete(ub);
        }
        currentUser.getMyBadges().clear();
        userRepository.save(currentUser);
    }

    @Transactional(readOnly = true)
    public Badge getBadgeByName(String badgeName) {
        return badgeRepository.findByName(badgeName).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Badge> getAllBadgesByActiveTrue() {
        return badgeRepository.findBadgesByActiveTrue();
    }
}
