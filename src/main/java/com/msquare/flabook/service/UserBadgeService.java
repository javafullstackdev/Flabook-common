package com.msquare.flabook.service;

import lombok.RequiredArgsConstructor;
import com.msquare.flabook.models.Badge;
import com.msquare.flabook.models.User;
import com.msquare.flabook.models.UserBadge;
import com.msquare.flabook.repository.UserBadgeRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class UserBadgeService {

    private final UserBadgeRepository userBadgeRepository;

    public boolean isPresentUserBadge(User currentUser, Badge badge){
        UserBadge userBadge = userBadgeRepository.findUserBadge(currentUser, badge).orElse(null);
        return !Objects.isNull(userBadge);
    }

}
