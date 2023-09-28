package com.bosch.digicore.exceptions;

public class NotBoschUserException extends RuntimeException {

    public NotBoschUserException(String username) {
        super("You are not Bosch user with username: " + username);
    }
}
