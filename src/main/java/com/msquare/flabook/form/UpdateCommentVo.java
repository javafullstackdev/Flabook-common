package com.msquare.flabook.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Data
@EqualsAndHashCode(callSuper = true)
@Validated
public class UpdateCommentVo extends BaseCommentVo {

    private Long ids;
    private MultipartFile files;
}
