package com.msquare.flabook.common.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import com.msquare.flabook.json.Views;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@JsonView(Views.BaseView.class)
@NoArgsConstructor
@AllArgsConstructor
public class Paging<T extends Serializable> implements Serializable
{
    private Long nextSinceId;
    private Long nextMaxId;

    private int count;
    @NonNull
    private List<T> list;

    @SuppressWarnings("unused")
    public Paging(int count, @NonNull List<T> list) {
        this.count = count;
        this.list = list;
    }

    public Paging(Long nextSinceId, Long nextMaxId, int count) {
        this.nextSinceId = nextSinceId;
        this.nextMaxId = nextMaxId;
        this.count = count;
        this.list = Collections.emptyList();
    }
}
