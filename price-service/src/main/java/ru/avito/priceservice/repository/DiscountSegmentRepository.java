package ru.avito.priceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.avito.priceservice.entity.DiscountSegment;
import ru.avito.priceservice.entity.User;

import java.util.List;

public interface DiscountSegmentRepository extends JpaRepository<DiscountSegment, Long> {
    List<DiscountSegment> findByUser(User user);
}
