package com.msquare.flabook.service;

import com.drew.imaging.ImageProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.dto.NoticeDto;
import com.msquare.flabook.enumeration.NoticeStatus;
import com.msquare.flabook.enumeration.NoticeType;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.form.CreateNoticeVo;
import com.msquare.flabook.form.UpdateNoticeVo;
import com.msquare.flabook.models.Notice;
import com.msquare.flabook.repository.NoticeRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PYS
 *
 * Dto 변환 전 Entity 를 그대로 반환하는 서비스 레이어를 하나 더 두는게 나을 듯
 */
@Slf4j
@Service
@AllArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ImageUploadService imageUploadService;

    private void addImageResource(Notice notice, UpdateNoticeVo vo) {
		if(vo.getFiles() != null && !vo.getFiles().isEmpty()) {
			try {
				ImageUploadService.ImageResourceInfoWithMetadata resourceInfoWithMetadata = imageUploadService.uploadWithMeta(FolderDatePatterns.NOTICES, vo.getFiles());
				notice.setImageResource(resourceInfoWithMetadata.getImageResource());
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			} catch(IOException | ImageProcessingException e) {
				e.printStackTrace();
			}
		}
	}


	private void addStart(Notice notice, UpdateNoticeVo vo) {
		if(vo.getStart() != null) {
			notice.setStart(vo.getStart());
		} else {
			notice.setStart(ZonedDateTime.now());
		}
	}

	private void addEnd(Notice notice, UpdateNoticeVo vo) {
		if(vo.getEnd() != null) {
			notice.setEnd(vo.getEnd());
		} else {
			notice.setEnd(ZonedDateTime.now().plusYears(1));
		}
	}

	@Transactional
    public Notice createNotice(CreateNoticeVo vo) {
        Notice notice = new Notice();
        notice.setTitle(vo.getTitle());
        notice.setDescription("<header><meta name=\"viewport\" content=\"user-scalable=yes, width=device-width\"><style>img{max-width: 100%;height:auto !important;}</style></header>"+vo.getDescription());
        notice.setStatus(vo.getStatus());
        notice.setUrl(vo.getUrl());
        notice.setType(vo.getType());

        if(vo.getPopup() != null) {
        	log.info("팝업 공지사항 등록");
        	//팝업으로 띄우기 설정을 하였을 경우
        	notice.setPopup(vo.getPopup());
        	notice.setInterval(vo.getInterval());

			addImageResource(notice, vo);
        	addStart(notice, vo);
        	addEnd(notice, vo);

        }else {
        	log.info("공지사항 등록");
        }

        return noticeRepository.save(notice);
    }

    @Transactional
    public Notice updateNotice(Long id, UpdateNoticeVo vo) {

        Notice notice = noticeRepository.findById(id).orElseThrow(() -> new FlabookGlobalException("not found entity : " + id));

        notice.setTitle(vo.getTitle());
        notice.setDescription(vo.getDescription());
        notice.setStatus(vo.getStatus());
        notice.setUrl(vo.getUrl());

		if(vo.getPopup() != null && vo.getPopup()) {
			log.info("팝업 공지사항 등록");
        	//팝업으로 띄우기 설정을 하였을 경우
        	notice.setPopup(true);
        	notice.setInterval(vo.getInterval());
			addImageResource(notice, vo);
			addStart(notice, vo);
			addEnd(notice, vo);

		}else {
        	log.info("공지사항 등록");
        	notice.setPopup(false);
        	notice.setInterval(0);
        	notice.setStart(ZonedDateTime.now());
        	notice.setEnd(ZonedDateTime.now());
        	notice.setImageResource(null);
        }

        return notice;
    }

	@Transactional
    public Notice deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id).orElseThrow(() -> new FlabookGlobalException("not found entity : " + id));
        noticeRepository.delete(notice);
        return notice;
    }

    public Notice findById(long id) {
    	return noticeRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Notice> findListByCurrentDate(NoticeStatus status, NoticeType type) {
        ZonedDateTime currentDate = ZonedDateTime.now();
        return noticeRepository.findListByCurrentDate(currentDate, status, type);
    }

    @Transactional(readOnly = true)
    public List<Notice> findList(NoticeStatus status, NoticeType type) {
    	return noticeRepository.findList(status, type);
    }

	/*어드민 추가*/
    @Transactional(readOnly = true)
    public List<NoticeDto> findList(NoticeType type, Pageable pageable) {
    	List<Notice> noticeList = noticeRepository.findList(type, pageable);
        return noticeList.stream().map(NoticeDto::of).collect(Collectors.toList());
    }

	@Transactional
    public Notice save(Notice notification) {
    	return noticeRepository.save(notification);
    }

	@Transactional
    public void delete(Notice notification) {
    	noticeRepository.delete(notification);
    }

	@Transactional(readOnly = true)
    public Long getCount(NoticeType type) {
    	return noticeRepository.count(type);
    }
    /*어드민 추가 END*/

}
