package org.example.cafecrm.service;

import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.analytics.AverageCheckDto;
import org.example.cafecrm.domain.dto.analytics.HourlyLoadDto;
import org.example.cafecrm.domain.dto.analytics.PopularItemsDto;
import org.example.cafecrm.repository.OrderItemRepository;
import org.example.cafecrm.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Сервис аналитики ресторана.
 * <p>
 * Предоставляет метрики по выручке, загрузке и популярности блюд.
 * Все методы работают только с закрытыми (оплаченными) заказами.
 *
 * @author AinLume
 */
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Средний чек за указанный период.
     *
     * @param start начало периода
     * @param end   конец периода
     * @return DTO со средним чеком и сопутствующими метриками
     */
    @Transactional(readOnly = true)
    public AverageCheckDto getAverageCheck(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

        Object[] result = orderRepository.calculateAverageCheck(startDateTime, endDateTime);
        Long totalRevenue = (Long) result[0];
        Long totalOrders = (Long) result[1];

        BigDecimal averageCheck = totalOrders > 0
                ? BigDecimal.valueOf(totalRevenue)
                .divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new AverageCheckDto(
                totalRevenue,
                totalOrders,
                averageCheck,
                start.format(DATE_FORMATTER),
                end.format(DATE_FORMATTER)
        );
    }

    /**
     * Загрузка ресторана по часам за период.
     *
     * @param start начало периода
     * @param end   конец периода
     * @return DTO с данными по каждому часу (0-23)
     */
    @Transactional(readOnly = true)
    public HourlyLoadDto getHourlyLoad(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

        List<Object[]> rawData = orderRepository.findHourlyLoad(startDateTime, endDateTime);

        List<HourlyLoadDto.HourlyData> hourlyData = rawData.stream()
                .map(row -> new HourlyLoadDto.HourlyData(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue()
                ))
                .toList();

        return new HourlyLoadDto(
                start.format(DATE_FORMATTER),
                end.format(DATE_FORMATTER),
                hourlyData
        );
    }

    /**
     * Популярные блюда за период (топ по выручке).
     *
     * @param start начало периода
     * @param end   конец периода
     * @return DTO с топом блюд
     */
    @Transactional(readOnly = true)
    public PopularItemsDto getPopularItems(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

        List<Object[]> rawData = orderItemRepository.findPopularItems(startDateTime, endDateTime);

        List<PopularItemsDto.ItemStat> topItems = rawData.stream()
                .map(row -> new PopularItemsDto.ItemStat(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).longValue(),
                        ((Number) row[4]).longValue(),
                        ((Number) row[5]).longValue()
                ))
                .toList();

        return new PopularItemsDto(
                start.format(DATE_FORMATTER),
                end.format(DATE_FORMATTER),
                topItems
        );
    }
}