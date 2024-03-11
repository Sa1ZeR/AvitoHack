package ru.avito.priceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.avito.priceservice.entity.StorageFile;

public interface StorageRepository extends JpaRepository<StorageFile, Long> {
}
