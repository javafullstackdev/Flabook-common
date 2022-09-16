package com.msquare.flabook.models;

import java.io.Serializable;
import java.time.ZonedDateTime;

public interface IUserCommentActivity extends Serializable {

    Long getId();

    UserCommentRelation getRelation();

    ZonedDateTime getCreatedAt();

}
