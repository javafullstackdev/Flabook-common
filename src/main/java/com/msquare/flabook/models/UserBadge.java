package com.msquare.flabook.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString
@NoArgsConstructor
public class UserBadge extends BaseEntity implements INotification{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "USER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "BADGE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Badge badge;


    public UserBadge(User user, Badge badge){
        this.user = user;
        this.badge = badge;
        this.setUser(user);
        this.setBadge(badge);
    }

    public void setUser(User user){
        user.getMyBadges().add(this);
    }

    public void setBadge(Badge badge){
        badge.getUsers().add(this);
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
        return this.badge.getTrueImageResource();
    }

    @Override
    public Resource asResource() {
        return new Resource(badge.getId(), Resource.ResourceType.badge, user.getId(), Resource.ResourceType.profile);
    }
}
