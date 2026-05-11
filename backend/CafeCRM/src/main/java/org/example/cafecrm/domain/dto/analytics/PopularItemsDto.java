package org.example.cafecrm.domain.dto.analytics;

import java.util.List;

/**
 * Популярные блюда за период с пагинацией.
 *
 * @param periodStart начало периода
 * @param periodEnd   конец периода
 * @param topItems    топ блюд на текущей странице
 * @param page        номер страницы (0-based)
 * @param size        размер страницы
 * @param totalItems  всего записей
 * @param totalPages  всего страниц
 */
public record PopularItemsDto(
        String periodStart,
        String periodEnd,
        List<ItemStat> topItems,
        int page,
        int size,
        long totalItems,
        int totalPages
) {

    public record ItemStat(
            Long menuItemId,
            String itemName,
            String categoryName,
            Long totalOrdered,
            Long totalQuantity,
            Long totalRevenue
    ) {}
}