package org.example.cafecrm.mapper;

import org.example.cafecrm.domain.dto.reservation.CreateReservationRequest;
import org.example.cafecrm.domain.dto.reservation.ReservationResponse;
import org.example.cafecrm.domain.entity.Reservation;
import org.example.cafecrm.domain.values.ReservationStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ReservationStatus.class)
public interface ReservationMapper {

    @Mapping(target = "reservationId", source = "id")
    @Mapping(target = "table", source = "table")
    ReservationResponse toResponse(Reservation reservation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "table", ignore = true)
    @Mapping(target = "staff", ignore = true)
    Reservation toEntity(CreateReservationRequest request);
}