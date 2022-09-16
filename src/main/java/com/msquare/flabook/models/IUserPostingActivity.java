package com.msquare.flabook.models;

import lombok.NonNull;
import com.msquare.flabook.enumeration.PostingType;

import java.io.Serializable;
import java.time.ZonedDateTime;

public interface IUserPostingActivity extends Serializable {
    Long getId();

    @NonNull
    UserPostingRelation getRelation();

    ZonedDateTime getCreatedAt();

    @NonNull
    PostingType getPostingType();
}
