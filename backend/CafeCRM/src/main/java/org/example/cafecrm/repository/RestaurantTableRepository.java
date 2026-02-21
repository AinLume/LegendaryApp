package org.example.cafecrm.repository;

import org.example.cafecrm.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Integer> {
}
