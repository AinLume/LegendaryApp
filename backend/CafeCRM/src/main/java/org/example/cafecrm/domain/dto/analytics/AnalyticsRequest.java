package org.example.cafecrm.domain.dto.analytics;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AnalyticsRequest(
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {}