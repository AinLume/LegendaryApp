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
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.order.CloseOrderRequest;
import org.example.cafecrm.domain.dto.order.CreateOrderRequest;
import org.example.cafecrm.domain.dto.order.OrderResponse;
import org.example.cafecrm.domain.values.OrderStatus;
import org.example.cafecrm.service.OrderService;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Управление заказами: создание, получение, закрытие, отмена")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Получить все заказы",
            description = "Возвращает страницу заказов с поддержкой пагинации, сортировки и фильтрации. " +
                    "Можно фильтровать по статусу, ID клиента и/или ID столика. " +
                    "Параметры пагинации передаются через query-параметры: page, size, sort. " +
                    "Доступно только администратору."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN")
    })
    public ResponseEntity<@NotNull Page<@NotNull OrderResponse>> getAll(
            @RequestParam(required = false)
            @Parameter(description = "Фильтр по статусу заказа", example = "NEW")
            OrderStatus status,

            @RequestParam(required = false)
            @Parameter(description = "Фильтр по ID клиента", example = "1")
            Long clientId,

            @RequestParam(required = false)
            @Parameter(description = "Фильтр по ID столика", example = "1")
            Integer tableId,

            @PageableDefault(size = 20)
            @Parameter(description = "Параметры пагинации и сортировки (page, size, sort)", example = "page=0&size=20&sort=createdAt,desc")
            Pageable pageable) {
        return ResponseEntity.ok(orderService.getAll(status, clientId, tableId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Получить заказ по ID",
            description = "Возвращает детальную информацию о заказе. Доступно только администратору."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN"),
            @ApiResponse(responseCode = "404", description = "Заказ с указанным id не найден")
    })
    public ResponseEntity<@NotNull OrderResponse> getById(
            @PathVariable
            @Parameter(description = "ID заказа", example = "1")
            Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping("/table/{tableId}")
    @PreAuthorize("hasAnyRole('WAITER')")
    @Operation(
            summary = "Получить заказы по столику",
            description = "Возвращает список заказов, привязанных к указанному столику. " +
                    "Актуально для заказов типа DINE_IN. Доступно только официантам."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль WAITER")
    })
    public ResponseEntity<@NotNull List<OrderResponse>> getByTable(
            @PathVariable
            @Parameter(description = "ID столика", example = "1")
            Integer tableId) {
        return ResponseEntity.ok(orderService.getByTable(tableId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Создать заказ",
            description = "Создаёт новый заказ. Автоматически определяет роль пользователя: " +
                    "если ROLE_CLIENT — clientId берётся из JWT, staffId = null; " +
                    "иначе staffId берётся из JWT, clientId — из запроса. " +
                    "Поддерживает типы DINE_IN (требуется tableId) и DELIVERY (требуется clientId + deliveryAddress). " +
                    "Доступно любому аутентифицированному пользователю."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Заказ успешно создан",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
            @ApiResponse(responseCode = "404", description = "Сотрудник, столик, клиент или позиция меню не найдены"),
            @ApiResponse(responseCode = "409", description = "Не указаны обязательные поля для выбранного типа заказа")
    })
    public ResponseEntity<@NotNull OrderResponse> create(
            @RequestBody @Valid
            @Parameter(description = "Данные для создания заказа", required = true)
            CreateOrderRequest request,

            @AuthenticationPrincipal
            @Parameter(description = "Аутентифицированный пользователь (из JWT)", hidden = true)
            UserDetails userDetails
    ) {
        boolean isClient = userDetails.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_CLIENT"));

        Long userId = Long.parseLong(userDetails.getUsername());

        Long staffId = isClient ? null : userId;
        Long clientId = isClient ? userId : request.clientId();

        return ResponseEntity.ok(orderService.create(request, staffId, clientId));
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Закрыть заказ",
            description = "Переводит заказ из статуса READY в PAID, фиксируя способ оплаты и время закрытия. " +
                    "Закрытие возможно только для заказов в статусе READY. " +
                    "Доступно любому аутентифицированному пользователю."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Заказ успешно закрыт",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
            @ApiResponse(responseCode = "404", description = "Заказ с указанным id не найден"),
            @ApiResponse(responseCode = "409", description = "Заказ не в статусе READY")
    })
    public ResponseEntity<@NotNull OrderResponse> close(
            @PathVariable
            @Parameter(description = "ID заказа", example = "1")
            Long id,

            @RequestBody @Valid
            @Parameter(description = "Данные о способе оплаты", required = true)
            CloseOrderRequest request
    ) {
        return ResponseEntity.ok(orderService.close(id, request));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Отменить заказ",
            description = "Переводит заказ в статус CANCELLED и фиксирует время отмены. " +
                    "Невозможно отменить уже оплаченный (PAID) или ранее отменённый заказ. " +
                    "Доступно любому аутентифицированному пользователю."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Заказ успешно отменён",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
            @ApiResponse(responseCode = "404", description = "Заказ с указанным id не найден"),
            @ApiResponse(responseCode = "409", description = "Заказ уже оплачен или отменён")
    })
    public ResponseEntity<@NotNull OrderResponse> cancel(
            @PathVariable
            @Parameter(description = "ID заказа", example = "1")
            Long id) {
        return ResponseEntity.ok(orderService.cancel(id));
    }
}