package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import com.msquare.flabook.dto.mapper.UserCommentRelationMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.UserCommentRelation;

import java.io.Serializable;

@Data
@Getter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.WebAdminJsonView.class)
public class UserCommentRelationDto implements Serializable {

	private static final long serialVersionUID = -900104790061831037L;
	private UserDto user;
    private CommentDto comment;

	public static UserCommentRelationDto of(UserCommentRelation userCommentRelation) {
        return  UserCommentRelationMapper.INSTANCE.of(userCommentRelation);
    }
}
