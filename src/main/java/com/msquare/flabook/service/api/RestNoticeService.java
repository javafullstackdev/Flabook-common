package com.msquare.flabook.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.dto.NoticeDto;
import com.msquare.flabook.enumeration.NoticeStatus;
import com.msquare.flabook.enumeration.NoticeType;
import com.msquare.flabook.form.CreateNoticeVo;
import com.msquare.flabook.form.UpdateNoticeVo;
import com.msquare.flabook.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestNoticeService<T extends NoticeDto> {

    private final NoticeService noticeService;

    public T createNotice(CreateNoticeVo vo) {
        return (T)T.of(noticeService.createNotice(vo));
    }

    public T updateNotice(Long id, UpdateNoticeVo vo) {
        return (T)T.of(noticeService.updateNotice(id, vo));
    }

    public List<T> findList(NoticeStatus status, NoticeType type) {
        return noticeService.findList(status, type).stream().map(n -> (T)T.of(n)).collect(Collectors.toList());
    }

    public List<T> findListByCurrentDate(NoticeStatus status, NoticeType type) {
    	return noticeService.findListByCurrentDate(status, type).stream().map(n -> (T)T.of(n)).collect(Collectors.toList());
    }

    public T deleteNotice(Long id) {
        return (T)T.of(noticeService.deleteNotice(id));
    }

}
