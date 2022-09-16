package com.msquare.flabook.dto;

import lombok.AllArgsConstructor;
import com.msquare.flabook.models.*;

import java.util.List;

@AllArgsConstructor
public class BadgeAfterDto implements INotification {

    List<UserBadge> userBadges;

    @Override
    public String getTitle() {
        return getOwner().getNickname()+"님의 새로운 뱃지가 획득되었습니다";
    }

    @Override
    public String getText() {
        String numberStr = userBadges.size() == 1 ? "" : String.format(" 외 %d개", userBadges.size());

        return  userBadges.stream().map(UserBadge::getBadge).map(Badge::getTitle)
                .findFirst().map(o -> o +"뱃지"+numberStr+"가 획득되었습니다.").orElse("뱃지가 획득되었습니다.");
    }

    @Override
    public User getOwner() {
        return userBadges.stream().map(UserBadge::getOwner).findFirst().orElse(null);
    }

    @Override
    public ImageResource getThumbnail() {
        return userBadges.stream().map(UserBadge::getThumbnail).findFirst().orElse(null);
    }

    @Override
    public Resource asResource() {
        return userBadges.stream().map(UserBadge::asResource).findFirst().orElse(null);
    }
}
