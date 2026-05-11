package org.example.cafecrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.analytics.AverageCheckDto;
import org.example.cafecrm.domain.dto.analytics.HourlyLoadDto;
import org.example.cafecrm.domain.dto.analytics.PopularItemsDto;
import org.example.cafecrm.service.AnalyticsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Аналитика ресторана: средний чек, почасовая загрузка, популярные блюда")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/average-check")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Средний чек за период",
            description = "Возвращает средний чек, общую выручку и количество заказов за указанный период. " +
                    "Учитываются только закрытые (оплаченные) заказы."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(schema = @Schema(implementation = AverageCheckDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные даты"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN")
    })
    public ResponseEntity<@NotNull AverageCheckDto> getAverageCheck(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Начало периода (YYYY-MM-DD)", example = "2024-01-01")
            LocalDate start,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Конец периода (YYYY-MM-DD)", example = "2024-01-31")
            LocalDate end) {

        return ResponseEntity.ok(analyticsService.getAverageCheck(start, end));
    }

    @GetMapping("/hourly-load")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Почасовая загрузка ресторана",
            description = "Возвращает статистику по количеству заказов и посетителей для каждого часа (0-23) " +
                    "за указанный период."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(schema = @Schema(implementation = HourlyLoadDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные даты"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN")
    })
    public ResponseEntity<@NotNull HourlyLoadDto> getHourlyLoad(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Начало периода (YYYY-MM-DD)", example = "2024-01-01")
            LocalDate start,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Конец периода (YYYY-MM-DD)", example = "2024-01-31")
            LocalDate end) {

        return ResponseEntity.ok(analyticsService.getHourlyLoad(start, end));
    }

    @GetMapping("/popular-items")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Популярные блюда за период",
            description = "Возвращает топ популярных блюд с количеством заказов и выручкой за указанный период. " +
                    "Поддерживается пагинация."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(schema = @Schema(implementation = PopularItemsDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные даты или параметры пагинации"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN")
    })
    public ResponseEntity<@NotNull PopularItemsDto> getPopularItems(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Начало периода (YYYY-MM-DD)", example = "2024-01-01")
            LocalDate start,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Конец периода (YYYY-MM-DD)", example = "2024-01-31")
            LocalDate end,

            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(analyticsService.getPopularItems(start, end, pageable));
    }
}