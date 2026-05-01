package org.example.cafecrm.repository;

import org.example.cafecrm.domain.entity.Tables;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantTableRepository extends JpaRepository<Tables, Integer> {
}
