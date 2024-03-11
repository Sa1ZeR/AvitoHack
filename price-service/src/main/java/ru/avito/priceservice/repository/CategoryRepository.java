package ru.avito.priceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.avito.priceservice.entity.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.category.id = :parentId")
    Optional<Category> findByParentId(Long parentId);

    Optional<Category> findByCategory(Category category);
}
