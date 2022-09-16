package com.msquare.flabook.controller;

import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.PostingDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/v2/logs")
@RestController
public class ClientLogController {
    @PostMapping(path = "")
    public CommonResponse<List<PostingDto>> doLogging(@RequestBody String body) {
        if(body != null && !body.isEmpty()) {
            log.info(body.replaceAll("(\r\n|\r|\n|\n\r)", ""));
        }
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), null);
    }
}
