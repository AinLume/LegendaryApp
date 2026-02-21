package org.example.cafecrm.repository;

import org.example.cafecrm.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
