package com.healthcheck.controller;

import com.healthcheck.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class WebhookController {

    private final AlertService alertService;

    @PostMapping("/alerts")
    public ResponseEntity<String> receiveAlerts(@RequestBody Map<String, Object> payload) {
        log.info("Webhook received from Alertmanager");
        alertService.processPrometheusAlerts(payload);
        return ResponseEntity.ok("OK");
    }
}
