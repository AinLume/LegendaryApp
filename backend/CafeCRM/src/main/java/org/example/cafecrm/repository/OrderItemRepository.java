package org.example.cafecrm.repository;

import org.example.cafecrm.domain.entity.OrderItem;
import org.example.cafecrm.domain.values.Destination;
import org.example.cafecrm.domain.values.OrderItemStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<@NotNull OrderItem, @NotNull Long> {

    List<OrderItem> findAllByDestination(Destination destination);

    List<OrderItem> findAllByDestinationAndStatus(Destination destination, OrderItemStatus status);

    List<OrderItem> findAllByOrderId(Long orderId);

    /**
     * Популярные блюда за период.
     */
    @Query("""
        SELECT oi.menuItem.id,
               oi.menuItem.name,
               oi.menuItem.category.name,
               COUNT(oi),
               SUM(oi.quantity),
               SUM(oi.menuItem.price * oi.quantity)
        FROM OrderItem oi
        JOIN oi.order o
        WHERE o.status = 'CLOSED'
        AND o.createdAt BETWEEN :start AND :end
        GROUP BY oi.menuItem.id, oi.menuItem.name, oi.menuItem.category.name
        ORDER BY SUM(oi.menuItem.price * oi.quantity) DESC
        """)
    List<Object[]> findPopularItems(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);
}
