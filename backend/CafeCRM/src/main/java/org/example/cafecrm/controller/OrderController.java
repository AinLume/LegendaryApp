package org.example.cafecrm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.order.CloseOrderRequest;
import org.example.cafecrm.domain.dto.order.CreateOrderRequest;
import org.example.cafecrm.domain.dto.order.OrderResponse;
import org.example.cafecrm.domain.values.OrderStatus;
import org.example.cafecrm.service.OrderService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<@NotNull List<OrderResponse>> getAll(@RequestParam(required = false) OrderStatus status) {
        if (status != null) {
            return ResponseEntity.ok(orderService.getAllByStatus(status));
        }
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NotNull OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping("/table/{tableId}")
    public ResponseEntity<@NotNull List<OrderResponse>> getByTable(@PathVariable Integer tableId) {
        return ResponseEntity.ok(orderService.getByTable(tableId));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<@NotNull List<OrderResponse>> getByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(orderService.getByClient(clientId));
    }

    @PostMapping
    public ResponseEntity<@NotNull OrderResponse> create(
            @RequestBody @Valid CreateOrderRequest request,
            @RequestAttribute Long staffId
    ) {
        return ResponseEntity.ok(orderService.create(request, staffId));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<@NotNull OrderResponse> close(
            @PathVariable Long id,
            @RequestBody @Valid CloseOrderRequest request
    ) {
        return ResponseEntity.ok(orderService.close(id, request));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<@NotNull OrderResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancel(id));
    }
}