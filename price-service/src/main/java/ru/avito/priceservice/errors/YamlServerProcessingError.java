package ru.avito.priceservice.errors;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class YamlServerProcessingError extends ResponseStatusException {
    public YamlServerProcessingError(JsonProcessingException e) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
