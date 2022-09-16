package com.msquare.flabook.queue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchQueryMessage implements Serializable {
    private String query;
    private Long userId;
}
