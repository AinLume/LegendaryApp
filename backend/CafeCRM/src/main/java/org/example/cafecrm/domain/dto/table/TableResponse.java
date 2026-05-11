package org.example.cafecrm.domain.dto.table;

public record TableResponse(
        Integer tableId,
        Integer number,
        Integer capacity,
        Integer posX,
        Integer posY,
        String status
) {}
