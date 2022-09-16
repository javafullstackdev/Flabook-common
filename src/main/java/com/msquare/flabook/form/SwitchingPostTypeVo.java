package com.msquare.flabook.form;

import lombok.Data;
import com.msquare.flabook.enumeration.PostingType;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class SwitchingPostTypeVo {
    private PostingType postingType;
}
