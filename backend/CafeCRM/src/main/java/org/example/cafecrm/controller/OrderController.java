package org.example.cafecrm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cafecrm.dto.CloseOrderRequest;
import org.example.cafecrm.dto.CreateOrderRequest;
import org.example.cafecrm.enums.OrderStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok("orderService.getAll()");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok("orderService.getById()");
    }

    @GetMapping("/table/{tableId}")
    public ResponseEntity<?> getByTable(@PathVariable Integer tableId) {
        return ResponseEntity.ok("orderService.getByTable()");
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<?> getByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok("orderService.getByClient()");
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateOrderRequest request) {
        return ResponseEntity.ok("orderService.create()");
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<?> close(@PathVariable Long id,
                                   @RequestBody @Valid CloseOrderRequest request) {
        return ResponseEntity.ok("orderService.close()");
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        return ResponseEntity.ok("orderService.cancel()");
    }
}
