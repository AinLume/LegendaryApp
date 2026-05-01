package org.example.cafecrm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.reservation.CreateReservationRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok("reservationService.getAll()");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok("reservationService.getById()");
    }

    @GetMapping("/available-tables")
    public ResponseEntity<?> getAvailableTables(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam Integer persons) {
        return ResponseEntity.ok("reservationService.getAvailableTables()");
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateReservationRequest request) {
        return ResponseEntity.ok("reservationService.create()");
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        return ResponseEntity.ok("reservationService.cancel()");
    }
}
