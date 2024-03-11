package ru.avito.priceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.avito.priceservice.entity.MapMatrix;

import java.util.Optional;

@Repository
public interface MapMatrixRepository extends JpaRepository<MapMatrix, Long> {
    Optional<MapMatrix> findByName(String name);

}
