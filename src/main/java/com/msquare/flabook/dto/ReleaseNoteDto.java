package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.mapper.ReleaseNoteMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.ReleaseNote;
import com.msquare.flabook.util.VersionComparer;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.BaseView.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReleaseNoteDto implements Serializable {

    private String version;
    private Boolean needUpdate;
    private Boolean forceUpdate;

    public static ReleaseNoteDto of(ReleaseNote releaseNote) {
        return ReleaseNoteMapper.INSTANCE.of(releaseNote);
    }

    public static ReleaseNoteDto of(VersionComparer.AppUpdateResult updateResult) {
        return ReleaseNoteMapper.INSTANCE.of(updateResult);
    }

}
