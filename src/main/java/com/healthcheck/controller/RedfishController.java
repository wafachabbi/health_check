package com.healthcheck.controller;

import com.healthcheck.service.RedfishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/redfish")
@RequiredArgsConstructor
public class RedfishController {

    private final RedfishService redfishService;

    @GetMapping("/health/{iloIp}")
    public ResponseEntity<Map<String, Object>> getServerHealth(@PathVariable String iloIp) {
        return ResponseEntity.ok(redfishService.getServerHealth(iloIp));
    }
}
