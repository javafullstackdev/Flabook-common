package com.msquare.flabook.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FlabookOException extends RuntimeException {
    public FlabookOException(String message) {
        super(message);
    }
}
