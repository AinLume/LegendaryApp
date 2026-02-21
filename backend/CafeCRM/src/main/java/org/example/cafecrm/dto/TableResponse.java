package org.example.cafecrm.dto;

public record TableResponse(
        Integer tableId,
        Integer number,
        Integer capacity,
        Integer posX,
        Integer posY,
        Integer status
) {}
