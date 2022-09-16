package com.msquare.flabook.controller;

import lombok.AllArgsConstructor;
import com.msquare.flabook.api.service.RankingCacheService;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.RankingResponseBodyDto;
import com.msquare.flabook.service.RankingService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@RequestMapping(path = "/v2/rankings")
@RestController
public class RankingController extends CorsController {

    private final RankingCacheService rankingCacheService;
    private final RankingService rankingService;

    @RequestMapping(path = "", produces = "application/json; charset=UTF8")
    public CommonResponse<RankingResponseBodyDto> doFindRankingList(HttpServletResponse response, @RequestParam(name = "rewrite", defaultValue = "false") boolean rewrite) {
        withCors(response);
        return new CommonResponse<>(CommonResponseCode.SUCCESS, rankingCacheService.findRanking(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Asia/Seoul")), !rewrite));
    }

    /**
     * 랭킹 순서 관리
     * */
    @PutMapping("/updateOrder")
    public void updateRankingOrder(@RequestParam("rankingGenres") List<String> genres){
        rankingService.updateRankingOrder(genres);
    }

    @GetMapping(path = "/order")
    public CommonResponse<List<String>> doFindRankingOrder(){
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), rankingService.getRankingOrder());
    }


}
