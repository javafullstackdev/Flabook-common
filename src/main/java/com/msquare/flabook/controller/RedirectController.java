package com.msquare.flabook.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.exception.FlabookPermissionException;
import com.msquare.flabook.models.Resource;
import com.msquare.flabook.service.RedirectLinkService;
import com.msquare.flabook.service.ShopApiService;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.msquare.flabook.configuration.TokenAuthorizationFilter.HEADER_STRING;

@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/v2/")
@RestController
public class RedirectController {

    private final RedirectLinkService redirectLinkService;
    private final ShopApiService shopApiService;


    private CommonResponse<String> getShopRedirectUrl(String accessToken, String resourceId) throws UnsupportedEncodingException {
        return new CommonResponse<>(CommonResponseCode.SUCCESS, shopApiService.generateRedirectUrlByResourceId(accessToken, resourceId));
    }

    private CommonResponse<String> getWebRedirectUrl(String accessToken, String resourceId) throws URISyntaxException {
        //랭킹 웹페이지 주소 반환
        if("ranking".equals(resourceId)) {
            return new CommonResponse<>(CommonResponseCode.SUCCESS, redirectLinkService.findPageUrl(resourceId));
        } else {

            //일반 주소 확인 후 auth url parameter 가 존재하는지 확인. auth 가 true 일 경우 사용자 토큰을 access_token 에 추가해보낸다.
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(resourceId);
            List<NameValuePair> params = URLEncodedUtils.parse(new URI(resourceId), StandardCharsets.UTF_8);
            for (NameValuePair param : params) {
                if ("auth".equals(param.getName())) {
                    if (Boolean.parseBoolean(param.getValue())) {
                        builder.queryParam("access_token", accessToken);
                    }
                } else {
                    builder.queryParam(param.getName(), param.getValue());
                }
            }
            return new CommonResponse<>(CommonResponseCode.SUCCESS, builder.toUriString());
        }
    }

    /**
     * resourceType 이 shop 인 resourceId 를 기반으로 이동할 페이지 주소를 조회
     * @param resourceType 리소스타입
     * @param resourceId 리소스ID
     * @return redirect주소
     */
    @GetMapping(path = "/page/redirect")
    public CommonResponse<String> doGetRedirectUrl(HttpServletRequest request, @RequestParam("resourceType") Resource.ResourceType resourceType, @RequestParam("resourceId") String resourceId) {
        try {
            String token = request.getHeader(HEADER_STRING);
            if (token == null) {
                throw new FlabookPermissionException("잘못된 접근입니다.");
            }

            String accessToken = token.substring(7);
            switch (resourceType) {
                case shop:
                    return getShopRedirectUrl(accessToken, resourceId);
                case web:
                    return getWebRedirectUrl(accessToken, resourceId);
                default:
                    return new CommonResponse<>(CommonResponseCode.SUCCESS, "https://www.flabook.co.kr");

            }
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            log.error("redirect", e);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, e.getMessage());
        }
    }


}
