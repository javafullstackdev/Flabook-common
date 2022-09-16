package com.msquare.flabook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@SuppressWarnings("java:S116")
@Slf4j
@Service
@RequiredArgsConstructor
public class RedirectLinkService {

    @Value("${flabook.policy.terms:https://cdn.flabook.co.kr/policy/terms.html}")
    private String POLICY_TERMS;

    @Value("${flabook.policy.location:https://cdn.flabook.co.kr/policy/location.html}")
    private String POLICY_LOCATION;

    @Value("${flabook.policy.privacy:https://flabook.co.kr/notice.html}")
    private String POLICY_PRIVACY;

    @Value("${flabook.policy.notice:https://cdn.flabook.co.kr/notice.html}")
    private String POLICY_NOTICE;

    @Value("${flabook.webpage.ranking:https://flabook-emergency.s3.ap-northeast-2.amazonaws.com/ranking/index.html}")
    private String WEBPAGE_RANKING;

    public String findPageUrl(String name) {
        if(name == null) return POLICY_TERMS;

        switch (name.toLowerCase()) {
            case "terms":
                return POLICY_TERMS;

            case "location":
                return POLICY_LOCATION;

            case "privacy":
                return POLICY_PRIVACY;

            case "ranking":
                return WEBPAGE_RANKING;

            case "notice":
            default:
                return POLICY_NOTICE;

        }
    }

}
