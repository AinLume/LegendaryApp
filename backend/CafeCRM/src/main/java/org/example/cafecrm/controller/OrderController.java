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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<@NotNull List<OrderResponse>> getAll(@RequestParam OrderStatus status) {
        if (status != null) {
            return ResponseEntity.ok(orderService.getAllByStatus(status));
        }
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<@NotNull OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping("/table/{tableId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    public ResponseEntity<@NotNull List<OrderResponse>> getByTable(@PathVariable Integer tableId) {
        return ResponseEntity.ok(orderService.getByTable(tableId));
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    public ResponseEntity<@NotNull List<OrderResponse>> getByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(orderService.getByClient(clientId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<@NotNull OrderResponse> create(
            @RequestBody @Valid CreateOrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean isClient = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

        Long userId = Long.parseLong(userDetails.getUsername());

        Long staffId = isClient ? null : userId;
        Long clientId = isClient ? userId : request.clientId();

        return ResponseEntity.ok(orderService.create(request, staffId, clientId));
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<@NotNull OrderResponse> close(
            @PathVariable Long id,
            @RequestBody @Valid CloseOrderRequest request
    ) {
        return ResponseEntity.ok(orderService.close(id, request));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<@NotNull OrderResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancel(id));
    }
}