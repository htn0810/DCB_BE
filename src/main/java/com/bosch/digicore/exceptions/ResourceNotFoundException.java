package com.bosch.digicore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String entityName, String attributeName, Object value) {
        super(String.format("%s with %s %s is not found", entityName, attributeName, value));
    }
}
