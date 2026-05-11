package org.example.cafecrm.domain.dto.reservation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.cafecrm.domain.values.ReservationType;

import java.time.LocalDateTime;

public record CreateReservationRequest(
        @NotNull
        Integer tableId,

        @NotBlank
        @Size(max = 50)
        String guestName,

        @Size(max = 20)
        String guestPhone,

        @NotNull
        @Min(1)
        Integer persons,

        @NotNull
        LocalDateTime startTime,

        @NotNull
        LocalDateTime endTime,

        @NotNull
        ReservationType type,

        String note
) {}
