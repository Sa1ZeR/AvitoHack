package ru.avito.priceservice.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CategoryNotFoundError extends ResponseStatusException {
    public CategoryNotFoundError() {
        super(HttpStatus.NOT_FOUND, "category not found");
    }
}
