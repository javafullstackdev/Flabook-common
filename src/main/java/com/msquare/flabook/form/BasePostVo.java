package com.msquare.flabook.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;


@Data
class BasePostVo {

    private String title;

    @NotEmpty
    @Length(min = 5, max = 8192)
    private String text;

    /**
     *
     * 자유게시판은 사진 필수 아님
     * @NotEmpty(message = "파일을 선택해주세요.")
     */
    private MultipartFile[] files;

    //가이드북용
    private MultipartFile[] posters;

    private String[] descriptions;
}
