package com.msquare.flabook.form;

import lombok.Data;
import com.msquare.flabook.enumeration.PostingType;

@Data
public class UpdatePhotoVo {

    private PostingType postingType;
    private String text;

}
