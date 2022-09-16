package com.msquare.flabook.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

@Data
@EqualsAndHashCode(callSuper = true)
@Validated
public class CreateAdoptVo extends BaseCommentVo {

}
