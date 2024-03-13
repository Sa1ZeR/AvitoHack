package ru.avito.priceservice.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.avito.priceservice.dto.Storage;
import ru.avito.priceservice.errors.YamlBadFormatError;
import ru.avito.priceservice.service.StorageService;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/storage")
public class StorageController {

    private final ObjectMapper yamlObjectMapper;
    private final StorageService service;

    @PostMapping
    public ResponseEntity<?> storage(@RequestBody String storageYaml) {
        try {
            yamlObjectMapper.readValue(storageYaml, Storage.class);
        } catch (JsonProcessingException e) {
            throw new YamlBadFormatError(e);
        }

        service.add(storageYaml);

        return ResponseEntity.status(HttpStatus.CREATED).body("Storage changed");
    }
}
