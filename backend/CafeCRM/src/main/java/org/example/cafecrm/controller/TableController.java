package org.example.cafecrm.controller;

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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
@Validated
@Slf4j
public class TableController {

    private final TableService tableService;

    @PostMapping
    public ResponseEntity<@NotNull TableResponse> create(@RequestBody @Valid CreateTableRequest request) {

        log.info("TableController:create.start, dto: {}", request);

        TableResponse response = tableService.create(request);

        log.info("TableController:create.end, response: {}", response);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<@NotNull TableResponse> delete(@PathVariable Integer id) {

        log.info("TableController:delete.start, id: {}", id);

        tableService.deleteTableById(id);

        log.info("TableController:delete.end, id: {}", id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<@NotNull List<TableResponse>> getAllTables() {

        log.info("TableController:getAllTables.start");

        List<TableResponse> response = tableService.findAll();

        log.info("TableController:getAllTables.end, count: {}", response.size());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/position")
    public ResponseEntity<@NotNull TableResponse> updatePosition(@PathVariable Integer id,
                                            @RequestBody @Valid UpdateTablePositionRequest request) {

        log.info("TableController:updatePosition.start, id: {}, dto: {}", id, request);

        TableResponse response = tableService.updateTablePosition(id, request);

        log.info("TableController:updatePosition.end, response: {}", response);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<@NotNull TableResponse> updateStatus(@PathVariable Integer id,
                                          @RequestBody @Valid UpdateTableStatusRequest request) {

        log.info("TableController:updateStatus.start, id: {}, dto: {}", id, request);

        TableResponse response = tableService.updateTableStatusById(id, request);

        log.info("TableController:updateStatus.end, response: {}", response);

        return ResponseEntity.ok(response);
    }
}
