package org.example.cafecrm.dto;

import org.example.cafecrm.enums.ReservationStatus;
import org.example.cafecrm.enums.ReservationType;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long reservationId,
        TableShortResponse table,
        String guestName,
        String guestPhone,
        Integer persons,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ReservationType type,
        ReservationStatus status,
        String note,
        LocalDateTime createdAt
) {}
