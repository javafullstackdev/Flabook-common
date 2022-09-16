package com.msquare.flabook.api.service;

import com.msquare.flabook.dto.RankingResponseBodyDto;

import java.time.ZonedDateTime;

public interface RankingCacheService {
    RankingResponseBodyDto findRanking(ZonedDateTime date, boolean rewrite);
}
