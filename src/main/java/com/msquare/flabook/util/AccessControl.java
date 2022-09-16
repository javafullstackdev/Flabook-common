package com.msquare.flabook.util;

import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.enumeration.UserRole;
import com.msquare.flabook.models.User;

@Slf4j
public class AccessControl {

    private AccessControl() {
        throw new IllegalStateException("AccessControl class");
    }

    public static boolean isAllowed(User contentOwner, User accessUser) {
        if(log.isDebugEnabled()) {
            log.debug("isAllowed {}, {}", contentOwner, accessUser);
        }
        return accessUser.isActive() && (UserRole.ADMIN.equals(accessUser.getRole())
                || (isAllowedUser(accessUser) && contentOwner.equals(accessUser)));
    }

    private static boolean isAllowedUser(User accessUser) {
        return UserRole.ADMIN.equals(accessUser.getRole())
                || UserRole.EXPERT.equals(accessUser.getRole())
                || UserRole.USER.equals(accessUser.getRole());
    }
}
