package com.msquare.flabook.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.NoticeDto;
import com.msquare.flabook.enumeration.NoticeStatus;
import com.msquare.flabook.enumeration.NoticeType;
import com.msquare.flabook.form.CreateNoticeVo;
import com.msquare.flabook.form.UpdateNoticeVo;
import com.msquare.flabook.service.api.RestNoticeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/events")
public class EventController {

	private final RestNoticeService<NoticeDto> noticeService;


	@GetMapping("")
	public CommonResponse<List<NoticeDto>> doFindListByCurrentDate() {
		return new CommonResponse<>(CommonResponseCode.SUCCESS, noticeService.findListByCurrentDate(NoticeStatus.open, NoticeType.event));
	}

	@GetMapping("/all")
	public CommonResponse<List<NoticeDto>> doFindList() {
		return new CommonResponse<>(CommonResponseCode.SUCCESS, noticeService.findList(NoticeStatus.open, NoticeType.event));
	}

	@PostMapping(value="")
	public CommonResponse<NoticeDto> doCreateNotice(CreateNoticeVo vo) {
		return new CommonResponse<>(CommonResponseCode.SUCCESS, noticeService.createNotice(vo));
	}

	@PostMapping(value="/{id}")
	public CommonResponse<NoticeDto> doUpdateNotice(@PathVariable Long id, UpdateNoticeVo vo) {
		return new CommonResponse<>(CommonResponseCode.SUCCESS, noticeService.updateNotice(id, vo));
	}

	@DeleteMapping(value="/{id}")
	public CommonResponse<NoticeDto> doDeleteNotice(@PathVariable Long id) {
		return new CommonResponse<>(CommonResponseCode.SUCCESS, noticeService.deleteNotice(id));
	}

}
