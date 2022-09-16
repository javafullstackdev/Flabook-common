package com.msquare.flabook.queue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SearchGroupingMessage implements Serializable {
    private String query;
    private long count;
}
