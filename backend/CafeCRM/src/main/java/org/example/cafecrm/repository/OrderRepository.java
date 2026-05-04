package org.example.cafecrm.repository;

import org.example.cafecrm.domain.entity.Order;
import org.example.cafecrm.domain.values.OrderStatus;
import org.example.cafecrm.domain.values.OrderType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<@NotNull Order, @NotNull Long> {

    List<Order> findAllByStatus(OrderStatus status);

    List<Order> findAllByTableId(Integer tableId);

    List<Order> findAllByClientId(Long clientId);

    /**
     * Средний чек и общая выручка за период (только закрытые/оплаченные заказы).
     */
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0), COUNT(o)
        FROM Order o
        WHERE o.status = 'CLOSED'
        AND o.closedAt BETWEEN :start AND :end
        """)
    Object[] calculateAverageCheck(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    /**
     * Загрузка по часам за период.
     */
    @Query("""
        SELECT HOUR(o.createdAt), COUNT(o), COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.status = 'CLOSED'
        AND o.createdAt BETWEEN :start AND :end
        GROUP BY HOUR(o.createdAt)
        ORDER BY HOUR(o.createdAt)
        """)
    List<Object[]> findHourlyLoad(@Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);
}
