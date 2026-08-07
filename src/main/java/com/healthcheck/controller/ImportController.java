package com.healthcheck.controller;

import com.healthcheck.model.Server;
import com.healthcheck.repository.ServerRepository;
import com.healthcheck.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImportController {

    private final ServerRepository serverRepository;
    private final AuditService auditService;

    @PostMapping("/servers/csv")
    public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file) {
        List<Server> servers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                String[] cols = line.split(",");
                if (cols.length < 5) continue;
                Server s = new Server();
                s.setName(cols[0].trim());
                s.setIpAddress(cols[1].trim());
                s.setIloIp(cols[2].trim());
                s.setModel(cols[3].trim());
                s.setLocation(cols[4].trim());
                s.setSerialNumber(cols.length > 5 ? cols[5].trim() : "");
                s.setStatus("UNKNOWN");
                servers.add(serverRepository.save(s));
            }
            auditService.log("system", "IMPORT_CSV", "servers", "Imported " + servers.size() + " servers from CSV", "system");
            return ResponseEntity.ok("Imported " + servers.size() + " servers");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
