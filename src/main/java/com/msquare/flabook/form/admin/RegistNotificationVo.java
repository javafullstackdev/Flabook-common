package com.msquare.flabook.form.admin;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.enumeration.PostingType;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RegistNotificationVo {

	private Long postingId;
	private PostingType postingType;
	private String deviceGroup;
	private String expertGroup;
	private String title;
	private String text;
	private String link;
	private MultipartFile files;

	private boolean isReserved;
	private ZonedDateTime reservedTime;

	public void setReservedTime(String reservedTime){
		this.reservedTime = ZonedDateTime.parse(reservedTime, DateTimeFormatter.RFC_1123_DATE_TIME);
	}
}
