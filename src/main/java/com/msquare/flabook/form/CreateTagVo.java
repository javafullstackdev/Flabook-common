package com.msquare.flabook.form;

import lombok.Data;
import com.msquare.flabook.models.Tag;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
public class CreateTagVo {

    @NotNull
    @Length(min = 1, max = 50)
    private String name;

    private Tag.Visibility visibility = Tag.Visibility.visible;
    private Tag.TagType tagType = Tag.TagType.none;
}
