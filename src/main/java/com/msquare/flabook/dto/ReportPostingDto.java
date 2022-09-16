package com.msquare.flabook.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.mapper.ReportPostingMapper;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.enumeration.ReportStatus;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.ReportPosting;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.WebAdminJsonView.class)
public class ReportPostingDto implements Serializable {

	private static final long serialVersionUID = -9222861211380658873L;

	private Long id;
    private PostingType postingType;

    private ReportStatus reportStatus;

    private UserPostingRelationDto relation;

    private String title;
    private String text;

    private ZonedDateTime createdAt;

    public ReportStatus getReportStatus() {
    	return reportStatus;
    }

    public PostingDto getPosting() {
        if(this.relation != null)
            return relation.getPosting();

        return null;
    }

    public UserDto getUser() {
        if(this.relation != null)
            return relation.getUser();

        return null;
    }

	public static ReportPostingDto of(ReportPosting reportPosting) {
		return ReportPostingMapper.INSTANCE.of(reportPosting);
	}
}
