package ru.avito.priceservice.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class LocationNotFoundError extends ResponseStatusException {
    public LocationNotFoundError() {
        super(HttpStatus.NOT_FOUND, "location not found");
    }
}
