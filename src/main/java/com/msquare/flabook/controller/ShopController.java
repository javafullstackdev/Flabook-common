package com.msquare.flabook.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.GoodsDto;
import com.msquare.flabook.dto.MetatagDto;
import com.msquare.flabook.dto.OrderDto;
import com.msquare.flabook.dto.ShopUserDto;
import com.msquare.flabook.dto.shop.DepositDto;
import com.msquare.flabook.dto.shop.MileageDto;
import com.msquare.flabook.dto.shop.WalletDto;
import com.msquare.flabook.exception.FlabookPermissionException;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.User;
import com.msquare.flabook.repository.UserRepository;
import com.msquare.flabook.service.NotificationService;
import com.msquare.flabook.service.ShopApiService;
import com.msquare.flabook.service.ShopService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.msquare.flabook.configuration.TokenAuthorizationFilter.HEADER_STRING;

@SuppressWarnings("java:S4684")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/v2/shops")
public class ShopController {

    @NonNull
    private final UserRepository userRepository;
    @NonNull
    private final ShopService shopService;
    @NonNull
    private final ShopApiService shopApiService;
    @NonNull
    private final NotificationService notificationService;

    @GetMapping(path = "/page/goods/{no}")
    public CommonResponse<String> doGetGoods(HttpServletRequest request, @PathVariable("no") String no) {
        try {
            String token = request.getHeader(HEADER_STRING);
            if (token == null) {
                throw new FlabookPermissionException(FlabookPermissionException.Messages.NOT_AUTHORIZED);
            }
            String accessToken = token.substring(7); //Bearer 제거
            return new CommonResponse<>(CommonResponseCode.SUCCESS, shopApiService.generateRedirectUrl(accessToken, shopApiService.generateGoodsUrl(no)));
        } catch (UnsupportedEncodingException e) {
            log.error("doGetGoods redirect", e);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, e.getMessage());
        }
    }

    /**
     * resourceType 이 shop 인 resourceId 를 기반으로 이동할 페이지 주소를 조회
     * @param resourceId 리소스ID
     * @return redirect 주소
     */
    @GetMapping(path = "/page/redirect")
    public CommonResponse<String> doGetRedirectUrl(HttpServletRequest request, @RequestParam("resourceId") String resourceId) {
        try {
            String token = request.getHeader(HEADER_STRING);
            if (token == null) {
                throw new FlabookPermissionException(FlabookPermissionException.Messages.NOT_AUTHORIZED);
            }
            String accessToken = token.substring(7); //Bearer 제거
            return new CommonResponse<>(CommonResponseCode.SUCCESS, shopApiService.generateRedirectUrlByResourceId(accessToken, resourceId));
        } catch (UnsupportedEncodingException e) {
            log.error("redirect", e);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, e.getMessage());
        }
    }

    @JsonView(Views.MyProfileDetailJsonView.class)
    @GetMapping(path = "/users/me")
    public CommonResponse<ShopUserDto> doFindMyProfile(@ApiIgnore User currentUser) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), ShopUserDto.of(currentUser));
    }

    @GetMapping(path = "/page/{name}")
    public CommonResponse<String> doGetPage(HttpServletRequest request, @PathVariable("name") String name) {
        try {
            String token = request.getHeader(HEADER_STRING);
            if (token == null) {
                throw new FlabookPermissionException(FlabookPermissionException.Messages.NOT_AUTHORIZED);
            }
            String accessToken = token.substring(7); //Bearer 제거

            return new CommonResponse<>(CommonResponseCode.SUCCESS, shopApiService.generateRedirectUrl(accessToken, shopApiService.generateUrlByName(name)));
        } catch (UnsupportedEncodingException e) {
            log.error("redirect", e);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, e.getMessage());
        }
    }

    /**
     * 예치금 조회
     * @param user 사용자
     * @return CommonResponse<DepositDto>
     */
    @GetMapping(path = "/deposit")
    public CommonResponse<DepositDto> doGetMyDeposit(User user) {
        DepositDto depositDto = shopApiService.requestGetDeposit(user);
        return new CommonResponse<>(CommonResponseCode.SUCCESS, depositDto);
    }

    /**
     * 예치금 조회
     * @param user 사용자
     * @return CommonResponse<MileageDto>
     */
    @GetMapping(path = "/mileage")
    public CommonResponse<MileageDto> doGetMyMileage(User user) {
        MileageDto mileageDto = shopApiService.requestGetMileage(user);
        return new CommonResponse<>(CommonResponseCode.SUCCESS, mileageDto);
    }

    /**
     * 예치금 조회
     * @param user 사용자
     * @return CommonResponse<WalletDto>
     */
    @GetMapping(path = "/wallet")
    public CommonResponse<WalletDto> doGetMyWallet(User user) {
        WalletDto walletDto = shopApiService.requestGetWallet(user);
        return new CommonResponse<>(CommonResponseCode.SUCCESS, walletDto);
    }

    @GetMapping(path = "/goods/{id}/meta")
    public MetatagDto newAd(@PathVariable("id") String id) {
        return shopApiService.requestSearchOrders(id);
    }

    @SuppressWarnings("java:S115")
    private enum Status {
        p1("결제완료"),
        d1("배송중"),
        d2("배송완료"),
        end("배송완료");

        @Getter
        private final String name;

        Status(String name) {
            this.name = name;
        }
    }
    private final Map<Long, Integer> sendSchedulers = new HashMap<>();

    @GetMapping(path = "/goods/test")
    public String send(User user) {
        sendSchedulers.computeIfAbsent(user.getId(), aLong -> 0);
        log.info("sendSchedulers : {}", sendSchedulers);
        return "잠시 후 발송됩니다.";
    }

    @Transactional
    @Scheduled(cron="*/10 * * * * *")
    public void scheduler() {

        Iterator<Map.Entry<Long, Integer>> it =  sendSchedulers.entrySet().iterator();
        if(sendSchedulers.size() > 0) {
            log.debug("배송정보 테스트 run");
        }

        while(it.hasNext()) {
            Map.Entry<Long, Integer> entry = it.next();
            Status status = Status.values()[entry.getValue()];
            log.debug("배송정보 테스트 {}, {}", entry.getKey(), status);
            if(Status.end.equals(status)) {
                sendSchedulers.remove(entry.getKey());
                continue;
            }

            userRepository.findById(entry.getKey()).ifPresent(user -> {
                OrderDto orderDto = new OrderDto("111111", "테스트 상품이 " + status.getName() + " 이(가) 되었습니다.", "http://flabook.godomall.com/data/goods/20/04/16/1000007798/1000007798_main_077.jpg", user );
                notificationService.afterNewOrder(orderDto, user);
            });
            sendSchedulers.put(entry.getKey(), entry.getValue() + 1);
        }
    }

    @GetMapping(path = "/goods/a")
    public String send2(@RequestParam("reindex") Boolean reindex, @RequestParam(value = "page", required = false) Integer page) {
        shopApiService.syncGoods(page, reindex);
        return "ok";
    }

    @GetMapping(path = "/goods")
    public CommonResponse<List<GoodsDto>> doSearch(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int count, @RequestParam(value = "q", required = false) String query, @RequestParam(value = "orderby", defaultValue = "id") String orderBy) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS, shopService.searchList(offset, count, query, Optional.empty(), orderBy));
    }

    @GetMapping(path = "/goods/count")
    public CommonResponse<Integer> doSearchResultCount(@RequestParam(value = "q", required = false) String query) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS, shopService.searchResultCount(query, Optional.empty(), "id"));
    }

    @GetMapping(path = "/goods/recommended")
    public CommonResponse<List<GoodsDto>> doSearchRecommended(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int count, @RequestParam(value = "q", required = false) String query, @RequestParam(value = "orderby", defaultValue = "id") String orderBy) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS, shopService.searchList(offset, count, query, Optional.of(true), orderBy));
    }

    @GetMapping(path = "/goods/recommended/count")
    public CommonResponse<Integer> doSearchRecommendedResultCount(@RequestParam(value = "q", required = false) String query) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS, shopService.searchResultCount(query, Optional.of(true), "id"));
    }

    @GetMapping(path = "/goods/index")
    public String index() {
        shopService.index();
        return "index";
    }
}
