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
@Tag(name = "Reservations", description = "Управление бронированиями столиков: создание, отмена, поиск доступных столиков")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Получить все бронирования",
            description = "Возвращает список всех бронирований. Доступно только администратору."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешно",
            content = @Content(schema = @Schema(implementation = ReservationResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN")
    public ResponseEntity<@NotNull List<ReservationResponse>> getAll() {
        return ResponseEntity.ok(reservationService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Получить бронирование по ID",
            description = "Возвращает детальную информацию о бронировании. Доступно только администратору."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(schema = @Schema(implementation = ReservationResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN"),
            @ApiResponse(responseCode = "404", description = "Бронирование с указанным id не найдено")
    })
    public ResponseEntity<@NotNull ReservationResponse> getById(
            @PathVariable
            @Parameter(description = "ID бронирования", example = "1")
            Long id) {
        return ResponseEntity.ok(reservationService.getById(id));
    }

    @GetMapping("/available-tables")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Найти доступные столики",
            description = "Возвращает список столиков, свободных на указанный временной интервал " +
                    "и вмещающих заданное количество персон. Доступно только администратору. " +
                    "Временной интервал должен быть корректным (startTime строго раньше endTime)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(schema = @Schema(implementation = TableShortResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректный временной интервал"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN")
    })
    public ResponseEntity<@NotNull List<TableShortResponse>> getAvailableTables(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Начало бронирования (ISO 8601)", example = "2024-06-15T19:00:00")
            LocalDateTime startTime,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Окончание бронирования (ISO 8601)", example = "2024-06-15T21:00:00")
            LocalDateTime endTime,

            @RequestParam
            @Parameter(description = "Требуемое количество мест", example = "4")
            Integer persons) {

        return ResponseEntity.ok(reservationService.getAvailableTables(startTime, endTime, persons));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    @Operation(
            summary = "Создать бронирование",
            description = "Создаёт новое бронирование столика. Доступно администраторам и официантам. " +
                    "Выполняет валидации: корректность временного интервала, достаточность вместимости столика, " +
                    "отсутствие конфликтующих бронирований. staffId извлекается из JWT-токена."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Бронирование успешно создано",
                    content = @Content(schema = @Schema(implementation = ReservationResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN или WAITER"),
            @ApiResponse(responseCode = "404", description = "Столик или сотрудник не найдены"),
            @ApiResponse(responseCode = "409", description = "Конфликт: некорректный интервал, недостаточная вместимость или столик занят")
    })
    public ResponseEntity<@NotNull ReservationResponse> create(
            @RequestBody @Valid
            @Parameter(description = "Данные для создания бронирования", required = true)
            CreateReservationRequest request,

            @AuthenticationPrincipal
            @Parameter(description = "Аутентифицированный сотрудник (из JWT)", hidden = true)
            UserDetails staff
    ) {
        Long staffId = Long.parseLong(staff.getUsername());

        return ResponseEntity.ok(reservationService.create(request, staffId));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Отменить бронирование",
            description = "Отменяет активное бронирование, переводя статус в CANCELLED. " +
                    "Доступно любому аутентифицированному пользователю. " +
                    "Невозможно отменить уже отменённое или прошедшее бронирование."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Бронирование успешно отменено",
                    content = @Content(schema = @Schema(implementation = ReservationResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
            @ApiResponse(responseCode = "404", description = "Бронирование с указанным id не найдено"),
            @ApiResponse(responseCode = "409", description = "Бронирование уже отменено или является прошедшим")
    })
    public ResponseEntity<@NotNull ReservationResponse> cancel(
            @PathVariable
            @Parameter(description = "ID бронирования", example = "1")
            Long id) {
        return ResponseEntity.ok(reservationService.cancel(id));
    }
}