package com.msquare.flabook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.msquare.flabook.enumeration.RecommendKeywordType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendKeywordDto {

    private Long id;
    private String name;
    private RecommendKeywordType type;
}
