package com.msquare.flabook.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.openapi.kakao.Address;
import com.msquare.flabook.openapi.kakao.KakaoOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOpenApiService {

    @Value("${openapi.kakao.adminKey:e2fa4e0eda720111954330cc123349bd}")
    private String adminKey;

    @NonNull
    private final KakaoOpenApi kakaoOpenApi;

    public Address coord2Address(String x, String y) {
        return kakaoOpenApi.coord2address("KakaoAK " + adminKey, "WGS84", x, y);
    }



}
