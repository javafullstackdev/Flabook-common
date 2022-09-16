package com.msquare.flabook.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class UserWithBadgeDto implements Serializable {

    private List<BadgeDto> allBadges;

}
