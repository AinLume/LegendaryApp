package org.example.cafecrm.domain.dto.projection;

/**
 * Проекция для популярных блюд.
 *
 * @param menuItemId id блюда
 * @param menuItemName название блюда
 * @param categoryName название категории
 * @param orderCount количество заказов с этим блюдом
 * @param totalQuantity общее количество порций
 * @param totalRevenue общая выручка
 */
public record PopularItemProjection(
        long menuItemId,
        String menuItemName,
        String categoryName,
        long orderCount,
        long totalQuantity,
        long totalRevenue
) {
}
