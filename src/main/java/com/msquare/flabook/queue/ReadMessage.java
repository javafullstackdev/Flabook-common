package com.msquare.flabook.queue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadMessage implements Serializable {
    private Long postingId;
    private Long userId;
}
