package com.healthcheck.service;

import com.healthcheck.model.AuditLog;
import com.healthcheck.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(String username, String action, String resource, String details, String ip) {
        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(action);
        log.setResource(resource);
        log.setDetails(details);
        log.setIpAddress(ip);
        auditLogRepository.save(log);
    }

    public List<AuditLog> getAll() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }

    public List<AuditLog> getByUser(String username) {
        return auditLogRepository.findByUsernameOrderByTimestampDesc(username);
    }
}
