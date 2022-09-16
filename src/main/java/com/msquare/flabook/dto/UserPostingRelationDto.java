package com.msquare.flabook.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.mapper.UserPostingRelationMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.UserPostingRelation;

@Data
@Getter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.WebAdminJsonView.class)
public class UserPostingRelationDto  implements Serializable {

	private static final long serialVersionUID = -900104790061831037L;
	private UserDto user;
    private PostingDto posting;

	public static UserPostingRelationDto of(UserPostingRelation userPostingRelation) {
        return  UserPostingRelationMapper.INSTANCE.of(userPostingRelation);
    }
}
