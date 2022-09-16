package com.msquare.flabook.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

import org.mapstruct.Mapper;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.msquare.flabook.dto.mapper.TagAdminMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.Tag;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@JsonView(Views.WebAdminJsonView.class)
@Mapper
public class TagAdminDto implements Serializable {

	private Long id;

	@NonNull
	private String name;

    private String originalName;

    private String url;

    private Tag.SearchContentType type;

    private Tag.TagType tagType;

    private Tag.Visibility visibility;
    private Long plantId;
    private Dictionary dictionary;

    private ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;

    @Data
    public static class Dictionary implements Serializable {
        private String description;
        private String thumbnail;
    }

    public static TagAdminDto of(Tag tag) {
        return TagAdminMapper.INSTANCE.of(tag);
    }

    public static TagAdminDto of(String name) {
        return new TagAdminDto(name);
    }
}
