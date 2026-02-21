package org.example.cafecrm.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AnalyticsRequest(
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {}