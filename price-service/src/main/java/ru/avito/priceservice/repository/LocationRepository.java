package ru.avito.priceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.avito.priceservice.entity.Location;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    @Query("SELECT l FROM Location l WHERE l.location.id = :parentId")
    Optional<Location> findByParentId(Long parentId);

    Optional<Location> findByLocation(Location location);

    @Query(value = "SELECT parent_id FROM locations WHERE id = ?", nativeQuery = true)
    Optional<Long> findParentIdById(Long id);

    @Query(value = """
        WITH RECURSIVE rec AS (
           SELECT parent_id
           FROM locations
           WHERE id = ?
           UNION
           SELECT l.parent_id
           FROM locations l
           JOIN rec ON rec.parent_id = l.id
           WHERE l.parent_id IS NOT NULL
        )
        SELECT parent_id FROM rec
    """, nativeQuery = true)
    List<Long> findAllParentIdsById(Long id);
}
