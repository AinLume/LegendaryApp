package org.example.cafecrm.service;

import org.example.cafecrm.domain.dto.analytics.AverageCheckDto;
import org.example.cafecrm.domain.dto.analytics.HourlyLoadDto;
import org.example.cafecrm.domain.dto.analytics.PopularItemsDto;
import org.example.cafecrm.domain.dto.projection.AverageCheckProjection;
import org.example.cafecrm.domain.dto.projection.HourlyLoadProjection;
import org.example.cafecrm.domain.dto.projection.PopularItemProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class AnalyticsServiceTest extends BaseServiceTest {

    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        analyticsService = new AnalyticsService(orderRepository, orderItemRepository);
        setUpBaseEntities();
    }

    @Test
    void getAverageCheck_shouldReturnAverageCheck() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);

        when(orderRepository.calculateAverageCheck(any(), any()))
                .thenReturn(new AverageCheckProjection(50000L, 10L));

        AverageCheckDto result = analyticsService.getAverageCheck(start, end);

        assertThat(result).isNotNull();
        assertThat(result.totalRevenue()).isEqualTo(50000L);
        assertThat(result.totalOrders()).isEqualTo(10L);
        assertThat(result.averageCheck()).isEqualByComparingTo("5000.00");

        verify(orderRepository).calculateAverageCheck(any(), any());
    }

    @Test
    void getAverageCheck_shouldReturnZeroWhenNoOrders() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);

        when(orderRepository.calculateAverageCheck(any(), any()))
                .thenReturn(new AverageCheckProjection(0L, 0L));

        AverageCheckDto result = analyticsService.getAverageCheck(start, end);

        assertThat(result).isNotNull();
        assertThat(result.totalRevenue()).isEqualTo(0L);
        assertThat(result.totalOrders()).isEqualTo(0L);
        assertThat(result.averageCheck()).isEqualByComparingTo("0.00");
    }

    @Test
    void getHourlyLoad_shouldReturnHourlyData() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);

        List<HourlyLoadProjection> projections = List.of(
                new HourlyLoadProjection(18, 50L, 200L),
                new HourlyLoadProjection(19, 60L, 250L),
                new HourlyLoadProjection(20, 55L, 220L)
        );

        when(orderRepository.findHourlyLoad(any(), any())).thenReturn(projections);

        HourlyLoadDto result = analyticsService.getHourlyLoad(start, end);

        assertThat(result).isNotNull();
        assertThat(result.hourlyData()).hasSize(3);
        assertThat(result.hourlyData().get(0).hour()).isEqualTo(18);
        assertThat(result.hourlyData().get(0).orderCount()).isEqualTo(50L);

        verify(orderRepository).findHourlyLoad(any(), any());
    }

    @Test
    void getHourlyLoad_shouldReturnEmptyList_whenNoData() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);

        when(orderRepository.findHourlyLoad(any(), any())).thenReturn(List.of());

        HourlyLoadDto result = analyticsService.getHourlyLoad(start, end);

        assertThat(result).isNotNull();
        assertThat(result.hourlyData()).isEmpty();
    }

    @Test
    void getPopularItems_shouldReturnPopularItems() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);

        List<PopularItemProjection> itemsData = List.of(
                new PopularItemProjection(1L, "Coffee", "Hot", 100L, 50L, 15000L),
                new PopularItemProjection(2L, "Cake", "Dessert", 80L, 40L, 12000L)
        );

        Page<PopularItemProjection> page = new PageImpl<>(itemsData);

        when(orderItemRepository.findPopularItems(any(), any(), any()))
                .thenReturn(page);

        PopularItemsDto result = analyticsService.getPopularItems(start, end, PageRequest.of(0, 10));

        assertThat(result).isNotNull();
        assertThat(result.topItems()).hasSize(2);
        assertThat(result.topItems().get(0).itemName()).isEqualTo("Coffee");
        assertThat(result.topItems().get(0).totalOrdered()).isEqualTo(100L);
        assertThat(result.topItems().get(0).totalQuantity()).isEqualTo(50L);
        assertThat(result.totalPages()).isEqualTo(1);

        verify(orderItemRepository).findPopularItems(any(), any(), any());
    }

    @Test
    void getPopularItems_shouldReturnEmptyList_whenNoData() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);

        Page<PopularItemProjection> emptyPage = new PageImpl<>(List.of());

        when(orderItemRepository.findPopularItems(any(), any(), any()))
                .thenReturn(emptyPage);

        PopularItemsDto result = analyticsService.getPopularItems(start, end, PageRequest.of(0, 10));

        assertThat(result).isNotNull();
        assertThat(result.topItems()).isEmpty();
        assertThat(result.totalItems()).isEqualTo(0);
    }

    @Test
    void getPopularItems_shouldHandlePagination() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);

        List<PopularItemProjection> itemsData = List.of(
                new PopularItemProjection(1L, "Item1", "Desc1", 10L, 5L, 1000L)
        );

        Page<PopularItemProjection> page = new PageImpl<>(itemsData, PageRequest.of(1, 10), 25);

        when(orderItemRepository.findPopularItems(any(), any(), any()))
                .thenReturn(page);

        PopularItemsDto result = analyticsService.getPopularItems(start, end, PageRequest.of(1, 10));

        assertThat(result).isNotNull();
        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalItems()).isEqualTo(25);
        assertThat(result.totalPages()).isEqualTo(3);
    }
}

