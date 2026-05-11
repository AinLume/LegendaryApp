package org.example.cafecrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cafecrm.domain.dto.table.CreateTableRequest;
import org.example.cafecrm.domain.dto.table.TableResponse;
import org.example.cafecrm.domain.dto.table.UpdateTablePositionRequest;
import org.example.cafecrm.domain.dto.table.UpdateTableStatusRequest;
import org.example.cafecrm.service.TableService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Tables", description = "Управление столиками: создание, получение, обновление статуса и позиции, удаление")
public class TableController {

    private final TableService tableService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Создать столик",
            description = "Создаёт новый столик с указанным номером, вместимостью и позицией. " +
                    "Начальный статус — FREE. Номер столика должен быть уникальным."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Столик успешно создан",
                    content = @Content(schema = @Schema(implementation = TableResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "409", description = "Столик с таким номером уже существует")
    })
    public ResponseEntity<@NotNull TableResponse> create(
            @RequestBody @Valid
            @Parameter(description = "Данные для создания столика", required = true)
            CreateTableRequest request) {

        log.info("TableController:create.start, dto: {}", request);

        TableResponse response = tableService.create(request);

        log.info("TableController:create.end, response: {}", response);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Удалить столик",
            description = "Физически удаляет столик по идентификатору."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Столик успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Столик с указанным id не найден")
    })
    public ResponseEntity<@NotNull TableResponse> delete(
            @PathVariable
            @Parameter(description = "ID столика", example = "1")
            Integer id) {

        log.info("TableController:delete.start, id: {}", id);

        tableService.deleteTableById(id);

        log.info("TableController:delete.end, id: {}", id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    @Operation(
            summary = "Получить все столики",
            description = "Возвращает список всех столиков. Может вернуть пустой список."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешно",
            content = @Content(schema = @Schema(implementation = TableResponse.class))
    )
    public ResponseEntity<@NotNull List<TableResponse>> getAllTables() {

        log.info("TableController:getAllTables.start");

        List<TableResponse> response = tableService.findAll();

        log.info("TableController:getAllTables.end, count: {}", response.size());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/position")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    @Operation(
            summary = "Обновить позицию столика",
            description = "Обновляет координаты столика на плане зала (posX, posY). " +
                    "Идемпотентная операция: если координаты не изменились, сохранение не выполняется."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Позиция обновлена (или не изменилась)",
                    content = @Content(schema = @Schema(implementation = TableResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные координаты"),
            @ApiResponse(responseCode = "404", description = "Столик с указанным id не найден")
    })
    public ResponseEntity<@NotNull TableResponse> updatePosition(
            @PathVariable
            @Parameter(description = "ID столика", example = "1")
            Integer id,

            @RequestBody @Valid
            @Parameter(description = "Новые координаты столика", required = true)
            UpdateTablePositionRequest request) {

        log.info("TableController:updatePosition.start, id: {}, dto: {}", id, request);

        TableResponse response = tableService.updateTablePosition(id, request);

        log.info("TableController:updatePosition.end, response: {}", response);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    @Operation(
            summary = "Обновить статус столика",
            description = "Обновляет статус столика (FREE, OCCUPIED, RESERVED и т.д.). " +
                    "Идемпотентная операция: если статус не изменился, сохранение не выполняется."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Статус обновлён (или не изменился)",
                    content = @Content(schema = @Schema(implementation = TableResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректный статус"),
            @ApiResponse(responseCode = "404", description = "Столик с указанным id не найден")
    })
    public ResponseEntity<@NotNull TableResponse> updateStatus(
            @PathVariable
            @Parameter(description = "ID столика", example = "1")
            Integer id,

            @RequestBody @Valid
            @Parameter(description = "Новый статус столика", required = true)
            UpdateTableStatusRequest request) {

        log.info("TableController:updateStatus.start, id: {}, dto: {}", id, request);

        TableResponse response = tableService.updateTableStatusById(id, request);

        log.info("TableController:updateStatus.end, response: {}", response);

        return ResponseEntity.ok(response);
    }
}