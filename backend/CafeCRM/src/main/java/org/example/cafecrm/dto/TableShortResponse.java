package org.example.cafecrm.dto;

public record TableShortResponse(
        Integer tableId,
        Integer number,
        Integer capacity
) {}