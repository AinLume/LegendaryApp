package org.example.cafecrm.domain.dto;

public record ErrorResponse(
        int code,
        String message,
        int httpStatus
) { }
