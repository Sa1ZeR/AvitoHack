package ru.avito.priceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.avito.priceservice.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.category.id = :parentId")
    Optional<Category> findByParentId(Long parentId);

    Optional<Category> findByCategory(Category category);


    @Query(value = "SELECT parent_id FROM category WHERE id = ?", nativeQuery = true)
    Optional<Long> findParentIdById(Long id);

    @Query(value = """
        WITH RECURSIVE rec AS (
           SELECT parent_id
           FROM category
           WHERE id = ?
           UNION
           SELECT c.parent_id
           FROM category c
           JOIN rec ON rec.parent_id = c.id
           WHERE c.parent_id IS NOT NULL
        )
        SELECT parent_id FROM rec
    """, nativeQuery = true)
    List<Long> findAllParentIdsById(Long id);
}
