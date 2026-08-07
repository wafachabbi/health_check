package com.healthcheck.controller;

import com.healthcheck.model.Alert;
import com.healthcheck.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<Alert> acknowledge(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.acknowledge(id));
    }
}
