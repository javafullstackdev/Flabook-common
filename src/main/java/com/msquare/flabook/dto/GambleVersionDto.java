package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.mapper.GambleVersionMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.GambleVersion;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.BaseView.class)
public class GambleVersionDto implements Serializable {

    private Long id;

    private int version;

    private ZonedDateTime date;

    public static GambleVersionDto of(GambleVersion gambleVersion) {
        return GambleVersionMapper.INSTANCE.of(gambleVersion);
    }
}
