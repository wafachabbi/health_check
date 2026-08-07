package com.healthcheck.controller;

import com.healthcheck.model.AuditLog;
import com.healthcheck.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public List<AuditLog> getAll() { return auditService.getAll(); }

    @GetMapping("/user/{username}")
    public List<AuditLog> getByUser(@PathVariable String username) {
        return auditService.getByUser(username);
    }
}
