package ru.avito.priceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.avito.priceservice.entity.DiscountSegment;
import ru.avito.priceservice.entity.User;

import java.util.List;

public interface DiscountSegmentRepository extends JpaRepository<DiscountSegment, Long> {
    List<DiscountSegment> findByUser(User user);
    @Query("select ds from DiscountSegment ds where ds.user.id = :user")
    List<DiscountSegment> findByUser(Long user);
}
