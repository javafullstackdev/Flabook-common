package com.msquare.flabook.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.GambleDto;
import com.msquare.flabook.dto.GambleResultDto;
import com.msquare.flabook.dto.ShareDto;
import com.msquare.flabook.dto.ShippingDto;
import com.msquare.flabook.form.auth.UserAddressVo;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.User;
import com.msquare.flabook.service.GambleService;
import com.msquare.flabook.service.UserService;
import com.msquare.flabook.util.JsonViewMapper;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletResponse;
import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.Optional;

@SuppressWarnings({"java:S4684", "unused"})
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/v1/gambles")
public class GambleController extends CorsController {

    private final GambleService gambleService;
    private final UserService userService;

    @GetMapping(path = "")
    public MappingJacksonValue doFindGambleList(ServletResponse res, User user, @RequestParam(value = "offset", defaultValue = "0") int offset, @RequestParam(value = "count", defaultValue = "10") int count, @RequestParam(value = "active", required = false, defaultValue = "true") Boolean active  , @RequestParam(value = "detail", defaultValue = "false") boolean detail) {
        withCors(res);
        return JsonViewMapper.MAPPER.apply(new CommonResponse<>(CommonResponseCode.SUCCESS, gambleService.findList(offset, count, Optional.of(ZonedDateTime.now()), Optional.ofNullable(active))), (detail) ? Views.WebAdminJsonView.class : Views.BaseView.class);
    }

    @DeleteMapping(path = "/results/{id}")
    public CommonResponse<Void> doDeleteResult(ServletResponse res, User user, @PathVariable("id") Long id, @RequestParam(value = "detail", defaultValue = "false") boolean detail) {
        withCors(res);
        gambleService.deleteResult(id, user);
        return new CommonResponse<>(CommonResponseCode.SUCCESS, null);
    }

    @GetMapping(path = "/{id}")
    public MappingJacksonValue doFind(ServletResponse res, User user, @PathVariable("id") Long id, @RequestParam(value = "detail", defaultValue = "false") boolean detail) {
        withCors(res);
        return JsonViewMapper.MAPPER.apply(new CommonResponse<>(CommonResponseCode.SUCCESS, gambleService.findByUser(id, user)), (detail) ? Views.WebAdminJsonView.class : Views.BaseView.class);
    }

    @JsonView(Views.WebAdminJsonView.class)
    @PostMapping(path = "/{id}/init")
    public CommonResponse<GambleDto> doInit(ServletResponse res, User user, @PathVariable("id") Long id) {
        withCors(res);
        return new CommonResponse<>(CommonResponseCode.SUCCESS, gambleService.init(id));
    }

    @JsonView(Views.BaseView.class)
    @PostMapping(path = "/{id}/bet")
    public CommonResponse<GambleResultDto> doBat(ServletResponse res, User user,@PathVariable("id") Long id) {
        withCors(res);
        GambleService.BatResult result = gambleService.bet(id, user);
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), result.getGambleResult(), result.getMessage());
    }

    @PostMapping(path = "/{id}/share")
    public CommonResponse<ShareDto> doShare(ServletResponse res, User user, @PathVariable("id") Long id) {
        withCors(res);
        return new CommonResponse<>(CommonResponseCode.SUCCESS, gambleService.share(id, user));
    }

    @JsonView(Views.BaseView.class)
    @PostMapping(path = "/address")
    public CommonResponse<ShippingDto> doUpdateAddress(ServletResponse res, User user, @Valid @RequestBody UserAddressVo vo) {
        withCors(res);
        return new CommonResponse<>(CommonResponseCode.SUCCESS, userService.updateAddress(user, vo));
    }

    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/address")
    public CommonResponse<ShippingDto> doGetAddress(ServletResponse res, User user) {
        withCors(res);
        return new CommonResponse<>(CommonResponseCode.SUCCESS, userService.findAddress(user));
    }

}
