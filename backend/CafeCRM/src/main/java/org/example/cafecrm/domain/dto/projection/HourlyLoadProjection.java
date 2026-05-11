package org.example.cafecrm.domain.dto.projection;

/**
 * Проекция для загрузки по часам.
 *
 * @param hour час дня (0-23)
 * @param orderCount количество заказов
 * @param revenue выручка
 */
public record HourlyLoadProjection(
        int hour,
        long orderCount,
        long revenue
) {
}
