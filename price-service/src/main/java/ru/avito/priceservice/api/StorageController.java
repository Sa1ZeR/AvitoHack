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
import ru.avito.priceservice.cache.Cache;
import ru.avito.priceservice.dao.MatrixDao;
import ru.avito.priceservice.dto.Storage;
import ru.avito.priceservice.errors.YamlBadFormatError;
import ru.avito.priceservice.service.StorageService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/storage")
public class StorageController {

    private final ObjectMapper yamlObjectMapper;
    private final StorageService service;
    private final Cache cache;
    private final MatrixDao matrixDao;

    @PostMapping
    public ResponseEntity<?> storage(@RequestBody String storageYaml) {
        try {
            var storage = yamlObjectMapper.readValue(storageYaml, Storage.class);
            var discountsMatrix = storage.discounts().entrySet();
            for (var entry : discountsMatrix) {
                var categoryIds = matrixDao.findDistinctCategoryIds(entry.getValue());
                var locationIds = matrixDao.findDistinctLocationIds(entry.getValue());
                if (categoryIds.size() < locationIds.size()) {
                    cache.addIdsForCategories(entry.getKey(), categoryIds);
                } else {
                    cache.addIdsForLocations(entry.getKey(), locationIds);
                }
            }
        } catch (JsonProcessingException e) {
            throw new YamlBadFormatError(e);
        }

        service.add(storageYaml);

        return ResponseEntity.status(HttpStatus.CREATED).body("Storage changed");
    }
}
