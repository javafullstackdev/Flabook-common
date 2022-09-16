package com.msquare.flabook.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@EqualsAndHashCode(callSuper = true)
public class UpdatePost extends CreatePostVo {

    private Long[] deleteIds;


}
