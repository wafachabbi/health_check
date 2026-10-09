package com.healthcheck.controller;

import com.healthcheck.model.Alert;
import com.healthcheck.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public List<Alert> getAll() { return alertService.getAll(); }

    @GetMapping("/status/{status}")
    public List<Alert> getByStatus(@PathVariable String status) {
        return alertService.getByStatus(status);
    }

    @GetMapping("/filter")
    public List<Alert> getWithFilters(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String serverName) {
        return alertService.getWithFilters(status, severity, serverName);
    }

    @GetMapping("/trends")
    public ResponseEntity<List<Object[]>> getTrends() {
        return ResponseEntity.ok(alertService.getTrends());
    }

    @GetMapping("/sla")
    public ResponseEntity<Map<String, Object>> getSla() {
        return ResponseEntity.ok(alertService.getSla());
    }

    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<Alert> acknowledge(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.acknowledge(id));
    }
}
