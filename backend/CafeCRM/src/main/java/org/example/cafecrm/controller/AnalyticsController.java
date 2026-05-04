package org.example.cafecrm.controller;

import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.analytics.AverageCheckDto;
import org.example.cafecrm.domain.dto.analytics.HourlyLoadDto;
import org.example.cafecrm.domain.dto.analytics.PopularItemsDto;
import org.example.cafecrm.service.AnalyticsService;
import org.jetbrains.annotations.NotNull;
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
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/average-check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<@NotNull AverageCheckDto> getAverageCheck(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return ResponseEntity.ok(analyticsService.getAverageCheck(start, end));
    }

    @GetMapping("/hourly-load")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<@NotNull HourlyLoadDto> getHourlyLoad(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return ResponseEntity.ok(analyticsService.getHourlyLoad(start, end));
    }

    @GetMapping("/popular-items")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<@NotNull PopularItemsDto> getPopularItems(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return ResponseEntity.ok(analyticsService.getPopularItems(start, end));
    }
}