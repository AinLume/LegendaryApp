package org.example.cafecrm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.order.UpdateOrderItemStatusRequest;
import org.example.cafecrm.domain.values.OrderItemStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    @GetMapping("/kitchen")
    public ResponseEntity<?> getKitchenItems(
            @RequestParam(required = false) OrderItemStatus status) {
        return ResponseEntity.ok("orderItemService.getByDestination(KITCHEN)");
    }

    @GetMapping("/bar")
    public ResponseEntity<?> getBarItems(
            @RequestParam(required = false) OrderItemStatus status) {
        return ResponseEntity.ok("orderItemService.getByDestination(BAR)");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestBody @Valid UpdateOrderItemStatusRequest request) {
        return ResponseEntity.ok("orderItemService.updateStatus()");
    }
}
