package org.example.cafecrm.service;

import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.analytics.AverageCheckDto;
import org.example.cafecrm.domain.dto.analytics.HourlyLoadDto;
import org.example.cafecrm.domain.dto.analytics.PopularItemsDto;
import org.example.cafecrm.domain.dto.projection.HourlyLoadProjection;
import org.example.cafecrm.domain.dto.projection.PopularItemProjection;
import org.example.cafecrm.domain.dto.projection.AverageCheckProjection;
import org.example.cafecrm.repository.OrderItemRepository;
import org.example.cafecrm.repository.OrderRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        AverageCheckProjection projection = orderRepository.calculateAverageCheck(startDateTime, endDateTime);
        long totalRevenue = projection.totalRevenue();
        long totalOrders = projection.totalOrders();

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

        List<HourlyLoadProjection> projections = orderRepository.findHourlyLoad(startDateTime, endDateTime);

        List<HourlyLoadDto.HourlyData> hourlyData = projections.stream()
                .map(p -> new HourlyLoadDto.HourlyData(
                        p.hour(),
                        p.orderCount(),
                        p.revenue()
                ))
                .toList();

        return new HourlyLoadDto(
                start.format(DATE_FORMATTER),
                end.format(DATE_FORMATTER),
                hourlyData
        );
    }

    /**
     * Популярные блюда за период с пагинацией.
     *
     * @param start    начало периода
     * @param end      конец периода
     * @param pageable параметры пагинации (page, size)
     * @return DTO с топом блюд и метаданными пагинации
     */
    @Transactional(readOnly = true)
    public PopularItemsDto getPopularItems(LocalDate start, LocalDate end, Pageable pageable) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

        Page<@NotNull PopularItemProjection> page = orderItemRepository.findPopularItems(startDateTime, endDateTime, pageable);

        List<PopularItemsDto.ItemStat> topItems = page.getContent().stream()
                .map(p -> new PopularItemsDto.ItemStat(
                        p.menuItemId(),
                        p.menuItemName(),
                        p.categoryName(),
                        p.orderCount(),
                        p.totalQuantity(),
                        p.totalRevenue()
                ))
                .toList();

        return new PopularItemsDto(
                start.format(DATE_FORMATTER),
                end.format(DATE_FORMATTER),
                topItems,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}