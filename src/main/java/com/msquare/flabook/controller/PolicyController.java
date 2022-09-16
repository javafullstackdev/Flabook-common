package com.msquare.flabook.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.service.RedirectLinkService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 공지사항, 이용약관, 개인정보취급방침, 위치정보이용약관 주소 조회
 */

@Slf4j
@RequestMapping("/v2/policies")
@AllArgsConstructor
@RestController
public class PolicyController {

    private final RedirectLinkService policyService;

    @RequestMapping("/page/{name}")
    public CommonResponse<String> doGetPage(@PathVariable("name") String name) {
        return new CommonResponse<>( CommonResponseCode.SUCCESS , policyService.findPageUrl(name));
    }

}
