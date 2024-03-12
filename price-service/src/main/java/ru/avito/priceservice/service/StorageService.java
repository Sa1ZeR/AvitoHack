package ru.avito.priceservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avito.priceservice.dto.Storage;
import ru.avito.priceservice.errors.StorageFileNotFoundError;
import ru.avito.priceservice.errors.YamlServerProcessingError;
import ru.avito.priceservice.repository.StorageRepository;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository repository;
    private final ObjectMapper yamlObjectMapper;

    public Storage getCurrentStorage() {
        //TODO офк у нас всегда один файл поэтому он находится под индексом 1
        // конечно можно добавить несколько и возвращать последнюю, но такого кейса не было
        var storageFile = repository.findById(1L)
                .orElseThrow(StorageFileNotFoundError::new);
        var yaml = storageFile.getFile();
        Storage storage;
        try {
            storage = yamlObjectMapper.readValue(yaml, Storage.class);
        } catch (JsonProcessingException e) {
            throw new YamlServerProcessingError(e);
        }
        return storage;
    }
}
