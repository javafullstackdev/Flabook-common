package com.msquare.flabook.openapi.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="KakaoAddress", url = "${openapi.kakao.address.host:https://dapi.kakao.com}")
public interface KakaoOpenApi {

    @GetMapping(path = "/v2/local/geo/coord2address.json")
    Address coord2address(@RequestHeader("Authorization") String accessToken,
                                @RequestParam(name = "input_coord", defaultValue = "WGS84") String inputCoord,
                                @RequestParam(name = "x") String x,
                                @RequestParam(name = "y") String y );

}
