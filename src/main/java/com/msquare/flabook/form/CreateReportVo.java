package com.msquare.flabook.form;

import lombok.Data;
import com.msquare.flabook.util.CommonUtils;

import javax.validation.constraints.NotEmpty;

@Data
public class CreateReportVo {

    @NotEmpty
    private String title;

    private String text;


    public String getTitle() {
        return CommonUtils.convertDotString(this.title, 32);
    }

    public String getText() {
        return CommonUtils.convertDotString(this.title, 1024);
    }
}
