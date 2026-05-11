package org.example.cafecrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Order Items", description = "Управление позициями заказа: кухня, бар, обновление статуса приготовления")
@SecurityRequirement(name = "bearerAuth")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping("/kitchen")
    @PreAuthorize("hasAnyRole('COOK')")
    @Operation(
            summary = "Получить позиции для кухни",
            description = "Возвращает список позиций заказа, назначенных на кухню (Destination.KITCHEN). " +
                    "Опционально фильтрует по статусу приготовления. " +
                    "Доступно пользователю с ролью COOK."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(schema = @Schema(implementation = OrderItemResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Требуется аутентификация")
    })
    public ResponseEntity<@NotNull List<OrderItemResponse>> getKitchenItems(
            @RequestParam(required = false)
            @Parameter(description = "Фильтр по статусу приготовления (опционально)", example = "NEW")
            OrderItemStatus status
    ) {
        return ResponseEntity.ok(orderItemService.getKitchenItems(status));
    }

    @GetMapping("/bar")
    @PreAuthorize("hasAnyRole('BARTENDER')")
    @Operation(
            summary = "Получить позиции для бара",
            description = "Возвращает список позиций заказа, назначенных на бар (Destination.BAR). " +
                    "Опционально фильтрует по статусу приготовления. " +
                    "Доступно пользователю с ролью BARTENDER."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(schema = @Schema(implementation = OrderItemResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Требуется аутентификация")
    })
    public ResponseEntity<@NotNull List<OrderItemResponse>> getBarItems(
            @RequestParam(required = false)
            @Parameter(description = "Фильтр по статусу приготовления (опционально)", example = "NEW")
            OrderItemStatus status
    ) {
        return ResponseEntity.ok(orderItemService.getBarItems(status));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'COOK', 'BARTENDER')")
    @Operation(
            summary = "Обновить статус позиции заказа",
            description = "Обновляет статус приготовления позиции заказа. " +
                    "Допустимые переходы: NEW → IN_PROGRESS → READY. " +
                    "Из READY изменение запрещено. Пропуск IN_PROGRESS запрещён. " +
                    "После сохранения публикует событие для асинхронного обновления статуса родительского заказа. " +
                    "Доступно администраторам, поварам и барменам."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Статус успешно обновлён",
                    content = @Content(schema = @Schema(implementation = OrderItemResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN, COOK или BARTENDER"),
            @ApiResponse(responseCode = "404", description = "Позиция заказа с указанным id не найдена"),
            @ApiResponse(responseCode = "409", description = "Недопустимый переход статуса")
    })
    public ResponseEntity<@NotNull OrderItemResponse> updateStatus(
            @PathVariable
            @Parameter(description = "ID позиции заказа", example = "1")
            Long id,

            @RequestBody @Valid
            @Parameter(description = "Новый статус позиции", required = true)
            UpdateOrderItemStatusRequest request
    ) {
        return ResponseEntity.ok(orderItemService.updateStatus(id, request));
    }
}