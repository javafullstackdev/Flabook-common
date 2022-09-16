package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.msquare.flabook.enumeration.ExpertGroup;
import com.msquare.flabook.json.Views;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonView(Views.WebAdminJsonView.class)
public class UserExpertGroupDto implements Serializable {

    @JsonView(Views.WebAdminJsonView.class)
    private ExpertGroup expertGroup;
}
