package com.healthcheck.controller;

import com.healthcheck.model.Server;
import com.healthcheck.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServerController {

    private final ServerService serverService;

    @GetMapping
    public List<Server> getAll() { return serverService.getAll(); }

    @GetMapping("/filter")
    public List<Server> getWithFilters(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String environment,
            @RequestParam(required = false) String site,
            @RequestParam(required = false) String bay) {
        return serverService.getWithFilters(status, environment, site, bay);
    }

    @GetMapping("/firmware/non-compliant")
    public List<Server> getNonCompliantFirmware() {
        return serverService.getNonCompliantFirmware();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Server> getById(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Server> create(@RequestBody Server server) {
        return ResponseEntity.ok(serverService.create(server));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Server> update(@PathVariable Long id, @RequestBody Server server) {
        return ResponseEntity.ok(serverService.update(id, server));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serverService.delete(id);
        return ResponseEntity.ok().build();
    }
}
