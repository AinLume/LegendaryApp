package org.example.cafecrm.domain.dto.projection;

/**
 * Проекция для среднего чека.
 *
 * @param totalRevenue общая выручка
 * @param totalOrders количество заказов
 */
public record AverageCheckProjection(
        long totalRevenue,
        long totalOrders
) {
}
