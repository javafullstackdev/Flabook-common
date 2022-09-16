package com.msquare.flabook.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.msquare.flabook.enumeration.NoticeType;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = true)
@Validated
public class CreateNoticeVo extends UpdateNoticeVo {

    @NotEmpty
    private NoticeType type;
}
