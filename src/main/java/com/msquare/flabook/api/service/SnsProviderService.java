package com.msquare.flabook.api.service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.enumeration.UserProfileProviderType;
import com.msquare.flabook.exception.CommonException;
import com.msquare.flabook.models.SnsUserProfile;

@Slf4j
@Service
@AllArgsConstructor
public class SnsProviderService {

    private final RestTemplate restTemplate;
    private final Gson gson;

    /**
     * SNS 프로필 확인
     */
    public SnsUserProfile getSnsUserProfile(String accessToken, String provider) {
    	SnsUserProfile profile;
    	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);

        try {
	    	if(UserProfileProviderType.kakao.name().equals(provider)){
	    		profile = getKakaoProfile(request);
	    	}else if(UserProfileProviderType.naver.name().equals(provider)){
	    		profile = getNaverProfile(request);
	    	}else if(UserProfileProviderType.facebook.name().equals(provider)){
	    		profile = getFaceBookProfile(accessToken);
	    	}else if(UserProfileProviderType.apple.name().equals(provider)){
	    		profile = getAppleIdProfile(accessToken);
	    	}else {
	    		throw new CommonException(CommonResponseCode.USER_SNS_PROVIDER_NOT_SUPPORT, null);
	    	}
        } catch (Exception e) {
        	log.error("getSnsUserProfile", e);
        	throw new CommonException(CommonResponseCode.USER_SNS_GET_PROFILE_FAIL, null);
        }

    	return profile;
    }

    public SnsUserProfile getKakaoProfile(HttpEntity<MultiValueMap<String, String>> request) {

        ResponseEntity<String> response = restTemplate.postForEntity("https://kapi.kakao.com/v2/user/me", request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
        	return gson.fromJson(response.getBody(), SnsUserProfile.class);
        }

        throw new CommonException(CommonResponseCode.FAIL, null);
    }

    public SnsUserProfile getNaverProfile(HttpEntity<MultiValueMap<String, String>> request) {

    	ResponseEntity<?> response = restTemplate.postForEntity("https://openapi.naver.com/v1/nid/me", request, Map.class);
        if (response.getStatusCode() == HttpStatus.OK) {

        	//네이버의 경우 response객체에 userInfo를 보내기 때문에 한번 더 조회
            @SuppressWarnings("unchecked")
        	Map<String, Object> userAttributes = (Map<String, Object>)response.getBody();
            if(userAttributes != null && userAttributes.containsKey("response")) {
                return gson.fromJson(userAttributes.get("response").toString(), SnsUserProfile.class);
            }
        }

        throw new CommonException(CommonResponseCode.FAIL, null);
    }

    public SnsUserProfile getFaceBookProfile(String accessToken) {

        ResponseEntity<String> response = restTemplate.getForEntity("https://graph.facebook.com/me?access_token="+accessToken, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
        	return gson.fromJson(response.getBody(), SnsUserProfile.class);
        }

    	throw new CommonException(CommonResponseCode.FAIL, null);
    }

    public SnsUserProfile getAppleIdProfile(String accessToken) {

    	DecodedJWT jwt = JWT.decode(accessToken);
    	String id = jwt.getClaims().get("sub").asString();

        if(id != null) {
        	SnsUserProfile profile = new SnsUserProfile();
        	profile.setId(id);
        	return profile;
        }

    	throw new CommonException(CommonResponseCode.FAIL, null);
    }

}

