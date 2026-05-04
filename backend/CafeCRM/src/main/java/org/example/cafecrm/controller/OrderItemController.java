package org.example.cafecrm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.order.OrderItemResponse;
import org.example.cafecrm.domain.dto.order.UpdateOrderItemStatusRequest;
import org.example.cafecrm.domain.values.OrderItemStatus;
import org.example.cafecrm.service.OrderItemService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping("/kitchen")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<@NotNull List<OrderItemResponse>> getKitchenItems(
            @RequestParam(required = false) OrderItemStatus status
    ) {
        return ResponseEntity.ok(orderItemService.getKitchenItems(status));
    }

    @GetMapping("/bar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<@NotNull List<OrderItemResponse>> getBarItems(
            @RequestParam(required = false) OrderItemStatus status
    ) {
        return ResponseEntity.ok(orderItemService.getBarItems(status));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'COOK', 'BARTENER')")
    public ResponseEntity<@NotNull OrderItemResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateOrderItemStatusRequest request
    ) {
        return ResponseEntity.ok(orderItemService.updateStatus(id, request));
    }
}