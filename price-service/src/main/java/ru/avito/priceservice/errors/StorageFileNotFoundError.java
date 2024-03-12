package ru.avito.priceservice.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class StorageFileNotFoundError extends ResponseStatusException {
    public StorageFileNotFoundError() {
        super(HttpStatus.NOT_FOUND, "storage not found");
    }
}
