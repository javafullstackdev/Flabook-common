package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.common.configurations.ServiceHost;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LevelContainer {

    @Getter
    @NoArgsConstructor
    @JsonView(Views.BaseView.class)
    public static class LevelInfo implements Serializable {

        private String badge;

        private int level;

        @JsonIgnore
        private int max;

        @JsonIgnore
        private int min;

        private String name;

        private LevelInfo(int level, String name, String badge, int min, int max) {
            super();
            this.level = level;
            this.name = name;
            this.badge = badge;
            this.min = min;
            this.max = max;
        }
    }

    private static class LazyHolder { private static final LevelContainer INSTANCE =  new LevelContainer(); }

    public static LevelContainer getInstance() {
        return LazyHolder.INSTANCE;
    }

    private final Map<Integer, LevelInfo> levelInfos = new ConcurrentHashMap<>();

    private LevelContainer() {
        levelInfos.put(1, new LevelInfo(1, "일반", ServiceHost.getS3Url("commons/badges/badge0.png"), 0, 99));
        levelInfos.put(2, new LevelInfo(2, "초수", ServiceHost.getS3Url("commons/badges/badge1.png"), 100, 999));
        levelInfos.put(3, new LevelInfo(3, "중수", ServiceHost.getS3Url("commons/badges/badge2.png"), 1000, 4999));
        levelInfos.put(4, new LevelInfo(4, "고수", ServiceHost.getS3Url("commons/badges/badge3.png"), 5000, 9999));
        levelInfos.put(5, new LevelInfo(5, "만사마", ServiceHost.getS3Url("commons/badges/badge4.png"), 10000, 29999));
        levelInfos.put(6, new LevelInfo(6, "신", ServiceHost.getS3Url("commons/badges/badge5.png"), 30000, Integer.MAX_VALUE));
    }

    public LevelInfo findLevelInfo(int id) {
        return levelInfos.get(id);
    }

    public LevelInfo getLevel(int score) {
        LevelInfo result = findLevelInfo(1);
        for (Map.Entry<Integer, LevelInfo> entry : levelInfos.entrySet()) {
            LevelInfo info = entry.getValue();
            if (info.min <= score && score <= info.max) {
                result = info;
                break;
            }
        }
        return result;
    }

    public void setLevelInfo(LevelInfo info) {
        this.levelInfos.put(info.level, info);
    }
}
