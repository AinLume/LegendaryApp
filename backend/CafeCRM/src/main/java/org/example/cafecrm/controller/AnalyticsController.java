package org.example.cafecrm.controller;

import lombok.RequiredArgsConstructor;
import org.example.cafecrm.dto.AnalyticsRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    @GetMapping
    public ResponseEntity<?> getAnalytics(@RequestParam AnalyticsRequest period) {
        return ResponseEntity.ok("analyticsService.getAnalytics()");
    }
}