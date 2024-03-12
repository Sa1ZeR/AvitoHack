package ru.avito.priceservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avito.priceservice.dto.Storage;
import ru.avito.priceservice.entity.StorageFile;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class StorageService {

    public Storage getCurrentStorage() {
        //todo подумать как получать текущий сторадж
        return new Storage("", new HashMap<>());
    }
}
