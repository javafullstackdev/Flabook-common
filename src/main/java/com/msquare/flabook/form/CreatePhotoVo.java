package com.msquare.flabook.form;

import lombok.Data;
import com.msquare.flabook.enumeration.PostingType;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreatePhotoVo {
    private MultipartFile[] files;

    private String[] texts;

    private String[] albumNames;

    private PostingType postingType;
}
