package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.msquare.flabook.dto.mapper.ReportCommentMapper;
import com.msquare.flabook.enumeration.ReportStatus;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.ReportComment;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.WebAdminJsonView.class)
public class ReportCommentDto implements Serializable {

	private static final long serialVersionUID = -9222861211380658873L;

	private Long id;

    private ReportStatus reportStatus;

    private UserCommentRelationDto relation;

    private String title;
    private String text;

    private ZonedDateTime createdAt;

    @Accessors(chain = true)
    private Long postingId;

    public ReportStatus getReportStatus() {
    	return reportStatus;
    }

    public CommentDto getComment() {
        if(this.relation != null)
            return relation.getComment();

        return null;
    }

    public UserDto getUser() {
        if(this.relation != null)
            return relation.getUser();

        return null;
    }

	public static ReportCommentDto of(ReportComment reportComment) {
		return ReportCommentMapper.INSTANCE.of(reportComment);
	}
}
