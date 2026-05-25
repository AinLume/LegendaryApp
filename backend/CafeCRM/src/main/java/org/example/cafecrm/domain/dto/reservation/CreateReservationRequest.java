package org.example.cafecrm.domain.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
        LocalDateTime startTime,

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
        LocalDateTime endTime,

        @NotNull
        ReservationType type,

        String note
) {}
