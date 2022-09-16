package com.msquare.flabook.controller;

import lombok.AllArgsConstructor;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.BannerDto;
import com.msquare.flabook.enumeration.BannerStatus;
import com.msquare.flabook.form.CreateBannerVo;
import com.msquare.flabook.service.BannerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/v2/banners")
public class BannerController {

    private final BannerService bannerService;

    @RequestMapping("/create")
    public BannerDto create(@Valid CreateBannerVo vo) throws IOException, InterruptedException {
        return bannerService.createBanner(vo);
    }

    @RequestMapping("")
    public CommonResponse<List<BannerDto>> doFindList() {
        return new CommonResponse<>(CommonResponseCode.SUCCESS, bannerService.findList(0, 10, Optional.of(BannerStatus.open))) ;
    }



}
