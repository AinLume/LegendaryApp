package org.example.cafecrm.controller;

import jakarta.validation.Valid;
import org.example.cafecrm.dto.CreateTableRequest;
import org.example.cafecrm.dto.UpdateTablePositionRequest;
import org.example.cafecrm.dto.UpdateTableStatusRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateTableRequest request) {
        return ResponseEntity.ok("tableService.create()");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return ResponseEntity.ok("tableService.delete()");
    }

    @GetMapping
    public ResponseEntity<?> getAllTables() {
        return ResponseEntity.ok("tableService.getAllTables()");
    }

    @PutMapping("/{id}/position")
    public ResponseEntity<?> updatePosition(@PathVariable Integer id,
                                            @RequestBody @Valid UpdateTablePositionRequest request) {
        return ResponseEntity.ok("tableService.updatePosition()");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id,
                                          @RequestBody @Valid UpdateTableStatusRequest request) {
        return ResponseEntity.ok("tableService.updateStatus()");
    }
}
