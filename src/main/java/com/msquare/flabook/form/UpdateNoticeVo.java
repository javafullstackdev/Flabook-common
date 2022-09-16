package com.msquare.flabook.form;

import lombok.Data;
import com.msquare.flabook.enumeration.NoticeStatus;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Validated
public class UpdateNoticeVo {

    @NotEmpty
    @Length(min = 1, max = 254)
    private String title;

    @NotEmpty
    @Length(min = 1)
    private String description;

    private String url;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private ZonedDateTime start;

    public void setStart(String start){
		this.start = ZonedDateTime.parse(start, DateTimeFormatter.RFC_1123_DATE_TIME);
	}

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private ZonedDateTime end;

    public void setEnd(String end){
		this.end = ZonedDateTime.parse(end, DateTimeFormatter.RFC_1123_DATE_TIME);
	}

    private MultipartFile files;

    @NotEmpty
    private NoticeStatus status;

    @NotEmpty
    private int interval;

    private Boolean popup;
}
