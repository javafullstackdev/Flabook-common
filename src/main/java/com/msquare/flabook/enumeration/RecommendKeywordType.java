package com.msquare.flabook.enumeration;

import lombok.Getter;

@SuppressWarnings("java:S115")
@Getter
public enum RecommendKeywordType {
    question(PostingType.question),

    boast(PostingType.boast),

    clinic(PostingType.clinic),

    guidebook(PostingType.guidebook),

    free(PostingType.free),

    magazine(PostingType.magazine),

    television(PostingType.television),

    photo(PostingType.photo),

    shop,

    dictionary;

    private String name;
    private PostingType postingType;

    RecommendKeywordType(PostingType postingType) {
        this.postingType = postingType;
        this.name = this.postingType.getName();
    }

    RecommendKeywordType() {

    }

    public static RecommendKeywordType findRecommendKeywordType(PostingType postingType) {
        try {
            if (postingType != null) {
                return RecommendKeywordType.valueOf(postingType.name());
            } else {
                return null;
            }
        } catch (IllegalArgumentException e) {
           return null;
        }
    }

    public static void setRecommendKeywordName(RecommendKeywordType type, String name){
        type.name = name;
    }

}
