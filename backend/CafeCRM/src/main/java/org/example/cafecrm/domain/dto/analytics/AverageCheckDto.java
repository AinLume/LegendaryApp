package org.example.cafecrm.domain.dto.analytics;

import java.math.BigDecimal;

/**
 * Метрики среднего чека за период.
 *
 * @param totalRevenue    общая выручка
 * @param totalOrders     количество заказов
 * @param averageCheck    средний чек (totalRevenue / totalOrders)
 * @param periodStart     начало периода
 * @param periodEnd       конец периода
 */
public record AverageCheckDto(
        Long totalRevenue,
        Long totalOrders,
        BigDecimal averageCheck,
        String periodStart,
        String periodEnd
) {}