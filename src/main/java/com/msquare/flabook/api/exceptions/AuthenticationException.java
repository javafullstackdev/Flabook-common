package com.msquare.flabook.api.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
}
