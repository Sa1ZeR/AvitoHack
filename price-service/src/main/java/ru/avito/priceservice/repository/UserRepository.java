package ru.avito.priceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.avito.priceservice.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
