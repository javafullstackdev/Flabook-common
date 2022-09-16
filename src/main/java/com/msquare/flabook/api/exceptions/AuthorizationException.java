package com.msquare.flabook.api.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuthorizationException extends Exception {
    public AuthorizationException(String message) {
        super(message);
    }
}
