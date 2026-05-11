package org.example.cafecrm.domain.dto.table;

public record TableShortResponse(
        Integer tableId,
        Integer number,
        Integer capacity
) {}