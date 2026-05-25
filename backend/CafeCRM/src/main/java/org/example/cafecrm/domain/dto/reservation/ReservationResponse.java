package org.example.cafecrm.domain.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
        LocalDateTime startTime,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
        LocalDateTime endTime,
        ReservationType type,
        ReservationStatus status,
        String note,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
        LocalDateTime createdAt
) {}
