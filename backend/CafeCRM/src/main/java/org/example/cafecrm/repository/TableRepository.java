package org.example.cafecrm.repository;

import org.example.cafecrm.domain.entity.Tables;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<@NotNull Tables, @NotNull Integer> {
    boolean existsByNumber(Integer number);
}
