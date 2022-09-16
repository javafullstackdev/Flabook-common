package com.msquare.flabook.enumeration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum ExpertGroup {
	@JsonProperty("all") ALL("전체"),
	@JsonProperty("normal") NORMAL("일반유저"),
    @JsonProperty("name") NAME("이름"),
    @JsonProperty("clinic") CLINIC("클리닉"),
    @JsonProperty("contents") CONTENTS("컨텐츠"),
	@JsonProperty("test") TEST("테스트");

    private String name;

    ExpertGroup(String name) {
        this.name = name;
    }
    public String getName() {
    	return this.name;
    }
}
