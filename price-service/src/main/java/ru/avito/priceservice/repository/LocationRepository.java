package ru.avito.priceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.avito.priceservice.entity.Location;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    @Query("SELECT l FROM Location l WHERE l.location.id = :parentId")
    Optional<Location> findByParentId(Long parentId);

    Optional<Location> findByLocation(Location location);

    @Query(value = "SELECT parent_id FROM locations WHERE id = ?", nativeQuery = true)
    Optional<Long> findParentIdById(Long id);
}
