package ru.avito.priceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.avito.priceservice.entity.StorageFile;

import java.util.Optional;

public interface StorageRepository extends JpaRepository<StorageFile, Long> {
    @Query("FROM StorageFile sf WHERE sf.id = (SELECT MAX(s.id) FROM StorageFile s)")
    Optional<StorageFile> findLast();
}
