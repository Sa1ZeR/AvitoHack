package ru.avito.priceservice.errors;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class YamlBadFormatError extends ResponseStatusException {
    public YamlBadFormatError(JsonProcessingException e) {
        super(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
