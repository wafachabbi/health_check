package com.healthcheck.controller;

import com.healthcheck.model.Alert;
import com.healthcheck.model.Server;
import com.healthcheck.repository.AlertRepository;
import com.healthcheck.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExportController {

    private final ServerRepository serverRepository;
    private final AlertRepository alertRepository;

    @GetMapping("/servers/csv")
    public ResponseEntity<byte[]> exportServers() {
        List<Server> servers = serverRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("id,name,ipAddress,iloIp,model,serialNumber,location,firmwareVersion,status,createdAt\n");
        for (Server s : servers) {
            sb.append(s.getId()).append(",")
              .append(s.getName()).append(",")
              .append(s.getIpAddress()).append(",")
              .append(s.getIloIp()).append(",")
              .append(s.getModel()).append(",")
              .append(s.getSerialNumber()).append(",")
              .append(s.getLocation()).append(",")
              .append(s.getFirmwareVersion()).append(",")
              .append(s.getStatus()).append(",")
              .append(s.getCreatedAt()).append("\n");
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=servers.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(sb.toString().getBytes());
    }

    @GetMapping("/alerts/csv")
    public ResponseEntity<byte[]> exportAlerts() {
        List<Alert> alerts = alertRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("id,serverName,alertName,severity,status,description,firedAt,resolvedAt\n");
        for (Alert a : alerts) {
            sb.append(a.getId()).append(",")
              .append(a.getServerName()).append(",")
              .append(a.getAlertName()).append(",")
              .append(a.getSeverity()).append(",")
              .append(a.getStatus()).append(",")
              .append(a.getDescription()).append(",")
              .append(a.getFiredAt()).append(",")
              .append(a.getResolvedAt()).append("\n");
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=alerts.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(sb.toString().getBytes());
    }
}
