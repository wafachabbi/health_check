package com.healthcheck.service;

import com.healthcheck.repository.AlertRepository;
import com.healthcheck.repository.AuditLogRepository;
import com.healthcheck.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataRetentionService {

    private final AlertRepository alertRepository;
    private final AuditLogRepository auditLogRepository;
    private final ReportRepository reportRepository;
    private final AuditService auditService;

    // Purge alertes résolues de plus de 3 mois — tous les dimanches à 2h00
    @Scheduled(cron = "0 0 2 * * SUN")
    @Transactional
    public void purgeOldAlerts() {
        LocalDateTime limit = LocalDateTime.now().minusMonths(3);
        var old = alertRepository.findByFiredAtBefore(limit);
        int count = old.size();
        alertRepository.deleteAll(old);
        log.info("Purged {} alerts older than 3 months", count);
        auditService.log("system", "PURGE_ALERTS", "alerts",
                "Purged " + count + " alerts older than " + limit, "system");
    }

    // Purge logs d'audit de plus de 1 an — le 1er de chaque mois à 3h00
    @Scheduled(cron = "0 0 3 1 * *")
    @Transactional
    public void purgeOldAuditLogs() {
        LocalDateTime limit = LocalDateTime.now().minusYears(1);
        var old = auditLogRepository.findByTimestampBefore(limit);
        int count = old.size();
        auditLogRepository.deleteAll(old);
        log.info("Purged {} audit logs older than 1 year", count);
        auditService.log("system", "PURGE_AUDIT", "audit_logs",
                "Purged " + count + " audit logs older than " + limit, "system");
    }

    // Purge rapports de plus de 1 an — le 1er de chaque mois à 4h00
    @Scheduled(cron = "0 0 4 1 * *")
    @Transactional
    public void purgeOldReports() {
        LocalDateTime limit = LocalDateTime.now().minusYears(1);
        var old = reportRepository.findByGeneratedAtBefore(limit);
        int count = old.size();
        reportRepository.deleteAll(old);
        log.info("Purged {} reports older than 1 year", count);
        auditService.log("system", "PURGE_REPORTS", "reports",
                "Purged " + count + " reports older than " + limit, "system");
    }
}
