package com.msquare.flabook.models;

import java.time.ZonedDateTime;

public interface IActivity {
    Long getId() ;
    ZonedDateTime getCreatedAt();
}
