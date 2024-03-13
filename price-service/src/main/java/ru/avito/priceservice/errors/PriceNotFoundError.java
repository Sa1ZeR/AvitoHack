package ru.avito.priceservice.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PriceNotFoundError extends ResponseStatusException {
    public PriceNotFoundError() {
        super(HttpStatus.NOT_FOUND, "price not found");
    }
}
