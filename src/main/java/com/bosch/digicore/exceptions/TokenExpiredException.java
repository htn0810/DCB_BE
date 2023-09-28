package com.bosch.digicore.exceptions;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException() {
        super("Token was expired");
    }
}
