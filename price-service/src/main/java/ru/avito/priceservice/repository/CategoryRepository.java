package ru.avito.priceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.avito.priceservice.entity.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM category c WHERE c.parent_id = :id")
    Optional<Category> findByParentId(Long id);

    Optional<Category> findByCategory(Category category);
}
