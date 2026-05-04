package org.example.cafecrm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.reservation.CreateReservationRequest;
import org.example.cafecrm.domain.dto.reservation.ReservationResponse;
import org.example.cafecrm.domain.dto.table.TableShortResponse;
import org.example.cafecrm.service.ReservationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<@NotNull List<ReservationResponse>> getAll() {
        return ResponseEntity.ok(reservationService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<@NotNull ReservationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getById(id));
    }

    @GetMapping("/available-tables")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<@NotNull List<TableShortResponse>> getAvailableTables(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam Integer persons) {

        return ResponseEntity.ok(reservationService.getAvailableTables(startTime, endTime, persons));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    public ResponseEntity<@NotNull ReservationResponse> create(
            @RequestBody @Valid CreateReservationRequest request,
            @AuthenticationPrincipal UserDetails staff
    ) {
        Long staffId = Long.parseLong(staff.getUsername());

        return ResponseEntity.ok(reservationService.create(request, staffId));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<@NotNull ReservationResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancel(id));
    }
}
