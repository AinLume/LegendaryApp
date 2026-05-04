package org.example.cafecrm.repository;

import org.example.cafecrm.domain.entity.OrderItem;
import org.example.cafecrm.domain.values.Destination;
import org.example.cafecrm.domain.values.OrderItemStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<@NotNull OrderItem, @NotNull Long> {

    List<OrderItem> findAllByDestination(Destination destination);

    List<OrderItem> findAllByDestinationAndStatus(Destination destination, OrderItemStatus status);

    List<OrderItem> findAllByOrderId(Long orderId);

}
