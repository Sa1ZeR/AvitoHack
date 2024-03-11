package ru.avito.priceservice.errors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ExceptionApiHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> unsupportedOperationType(ResponseStatusException exception) {
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(exception.getMessage());
    }

}
