package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.*;
import com.msquare.flabook.dto.mapper.TagMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.Tag;
import org.mapstruct.Mapper;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@JsonIgnoreProperties({"createdAt", "modifiedAt", "type", "tagType", "visibility"})
@JsonView(Views.BaseView.class)
@Mapper
public class TagDto implements Serializable {

    //https://blog.deliwind.com/posts/235
    @NonNull
    private String name;

    private String originalName;

    private String url;

    private Tag.SearchContentType type;

    private Tag.TagType tagType;

    private Tag.Visibility visibility;

    private Dictionary dictionary;

    @Data
    public static class Dictionary implements Serializable {
        private String description;
        private String thumbnail;
    }

    public String getName() {
        if (this.tagType != null && this.tagType == Tag.TagType.dictionary) {
            if (StringUtils.isNotBlank(this.originalName)) {
                return originalName;
            } else {
                return name;
            }
        } else {
            return name;
        }
    }

    public static TagDto of(Tag tag) {
        return TagMapper.INSTANCE.of(tag);
    }

    public static TagDto of(String name) {
        return new TagDto(name);
    }



}
