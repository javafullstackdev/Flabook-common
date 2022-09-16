package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import com.msquare.flabook.json.Views;

import java.io.Serializable;

@JsonView(Views.BaseView.class)
@Data
@NoArgsConstructor
public class AnswerDto implements Serializable {

    private String text;

    private String nickname;

    @JsonIgnore
    private Long postingId;

    @JsonIgnore
    private int level;

    public AnswerDto(@NonNull String text, @NonNull String nickname, @NonNull Long postingId, int level) {
        this.text = text;
        this.nickname = nickname;
        this.postingId = postingId;
        this.level = level;
    }

    private transient LevelContainer.LevelInfo levelInfo;

    public LevelContainer.LevelInfo getLevelInfo() {
        if(levelInfo != null)
            return levelInfo;

        return LevelContainer.getInstance().findLevelInfo(level);
    }

}
