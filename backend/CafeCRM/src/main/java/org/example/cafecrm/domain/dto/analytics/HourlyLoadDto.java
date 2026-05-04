package org.example.cafecrm.domain.dto.analytics;

import java.util.List;

/**
 * Загрузка ресторана по часам за период.
 *
 * @param periodStart начало периода
 * @param periodEnd   конец периода
 * @param hourlyData  данные по каждому часу
 */
public record HourlyLoadDto(
        String periodStart,
        String periodEnd,
        List<HourlyData> hourlyData
) {

    public record HourlyData(
            Integer hour,        // 0-23
            Long orderCount,     // количество заказов
            Long totalRevenue    // выручка за этот час
    ) {}
}