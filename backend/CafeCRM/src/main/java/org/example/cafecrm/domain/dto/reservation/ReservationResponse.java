package org.example.cafecrm.domain.dto.reservation;

import org.example.cafecrm.domain.dto.table.TableShortResponse;
import org.example.cafecrm.domain.values.ReservationStatus;
import org.example.cafecrm.domain.values.ReservationType;

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
