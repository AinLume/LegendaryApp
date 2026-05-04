package org.example.cafecrm.domain.dto.analytics;

import java.util.List;

/**
 * Популярные блюда за период.
 *
 * @param periodStart начало периода
 * @param periodEnd   конец периода
 * @param topItems    топ блюд
 */
public record PopularItemsDto(
        String periodStart,
        String periodEnd,
        List<ItemStat> topItems
) {

    public record ItemStat(
            Long menuItemId,
            String itemName,
            String categoryName,
            Long totalOrdered,   // сколько раз заказали
            Long totalQuantity,  // общее количество порций
            Long totalRevenue    // общая выручка
    ) {}
}