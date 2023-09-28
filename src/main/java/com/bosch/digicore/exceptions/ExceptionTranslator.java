package com.bosch.digicore.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionTranslator {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(final BadRequestException ex) {
        final Throwable cause = (ex.getCause() != null ? ex.getCause() : ex);
        log.debug("Handle bad request exception with cause ({}): {}", cause.getClass().getName(), cause.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(final ResourceNotFoundException ex) {
        log.debug("Handle ResourceNotFoundException with root cause: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(RequestTimeoutException.class)
    public ResponseEntity<String> handleRequestTimeoutException(final RequestTimeoutException ex) {
        log.debug("Handle RequestTimeoutException with root cause: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(ex.getMessage());
    }

    @ExceptionHandler(FailedDependencyException.class)
    public ResponseEntity<String> handleFailedDependencyException(final FailedDependencyException ex) {
        log.debug("Handle FailedDependencyException with root cause: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(ex.getMessage());
    }
}
