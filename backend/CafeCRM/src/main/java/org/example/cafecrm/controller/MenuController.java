package org.example.cafecrm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.menu.CreateMenuCategoryRequest;
import org.example.cafecrm.domain.dto.menu.CreateMenuItemRequest;
import org.example.cafecrm.domain.dto.menu.UpdateMenuItemRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok("menuItemService.getCategories()");
    }

    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody @Valid CreateMenuCategoryRequest request) {
        return ResponseEntity.ok("menuItemService.createCategory()");
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        return ResponseEntity.ok("menuItemService.deleteCategory()");
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) Integer categoryId) {
        return ResponseEntity.ok("menuItemService.getAll()");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok("menuItemService.getById()");
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateMenuItemRequest request) {
        return ResponseEntity.ok("menuItemService.create()");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody @Valid UpdateMenuItemRequest request) {
        return ResponseEntity.ok("menuItemService.update()");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return ResponseEntity.ok("menuItemService.delete()");
    }
}
