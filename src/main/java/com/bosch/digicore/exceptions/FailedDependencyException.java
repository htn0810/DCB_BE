package com.bosch.digicore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
public class FailedDependencyException extends RuntimeException {

    public FailedDependencyException(String message) {
        super(message);
    }
}
