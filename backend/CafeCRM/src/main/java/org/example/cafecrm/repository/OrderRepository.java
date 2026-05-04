package org.example.cafecrm.repository;

import org.example.cafecrm.domain.entity.Order;
import org.example.cafecrm.domain.values.OrderStatus;
import org.example.cafecrm.domain.values.OrderType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<@NotNull Order, @NotNull Long> {

    List<Order> findAllByStatus(OrderStatus status);

    List<Order> findAllByTableId(Integer tableId);

    List<Order> findAllByClientId(Long clientId);

    List<Order> findAllByStatusAndType(OrderStatus status, OrderType type);
}
