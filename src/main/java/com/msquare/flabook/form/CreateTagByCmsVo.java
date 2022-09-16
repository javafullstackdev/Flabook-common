package com.msquare.flabook.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotNull;

/**
 * 도감관리
 */
@Data
@Validated
public class CreateTagByCmsVo {
    @NotNull
    @Length(min = 1, max = 50)
    private String name;

    /**
     * 구분자 ';'
     */
    private String tags;
}
