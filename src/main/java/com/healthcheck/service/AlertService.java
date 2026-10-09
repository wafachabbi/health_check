package com.healthcheck.service;

import com.healthcheck.model.Alert;
import com.healthcheck.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;
    private final JavaMailSender mailSender;

    public void processPrometheusAlerts(Map<String, Object> payload) {
        List<Map<String, Object>> alerts = (List<Map<String, Object>>) payload.get("alerts");
        if (alerts == null) return;

        for (Map<String, Object> a : alerts) {
            Map<String, String> labels = (Map<String, String>) a.get("labels");
            Map<String, String> annotations = (Map<String, String>) a.get("annotations");
            String state = (String) a.get("status");

            Alert alert = new Alert();
            alert.setAlertName(labels.getOrDefault("alertname", "Unknown"));
            alert.setServerName(labels.getOrDefault("instance", "Unknown"));
            alert.setSeverity(labels.getOrDefault("severity", "warning"));
            alert.setDescription(annotations != null ? annotations.getOrDefault("summary", "") : "");
            alert.setStatus("firing".equals(state) ? "NEW" : "RESOLVED");
            alert.setFiredAt(LocalDateTime.now());

            if ("resolved".equals(state)) {
                alert.setResolvedAt(LocalDateTime.now());
            }

            alertRepository.save(alert);
            log.info("Alert saved: {} - {}", alert.getAlertName(), alert.getStatus());

            if ("firing".equals(state)) {
                sendAlertEmail(alert);
            }
        }
    }

    public List<Alert> getAll() { return alertRepository.findAll(); }

    public List<Alert> getByStatus(String status) { return alertRepository.findByStatus(status); }

    public List<Alert> getWithFilters(String status, String severity, String serverName) {
        return alertRepository.findWithFilters(status, severity, serverName);
    }

    public Alert acknowledge(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        alert.setStatus("ACKNOWLEDGED");
        return alertRepository.save(alert);
    }

    // Tendances : alertes par jour sur les 30 derniers jours
    public List<Object[]> getTrends() {
        return alertRepository.countAlertsByDay(LocalDateTime.now().minusDays(30));
    }

    // SLA / taux de disponibilité sur les 30 derniers jours
    public Map<String, Object> getSla() {
        LocalDateTime from = LocalDateTime.now().minusDays(30);
        long total = alertRepository.countTotalSince(from);
        long resolved = alertRepository.countResolvedSince(from);
        double resolutionRate = total > 0 ? (resolved * 100.0 / total) : 100.0;

        Map<String, Object> sla = new HashMap<>();
        sla.put("totalAlerts", total);
        sla.put("resolvedAlerts", resolved);
        sla.put("resolutionRate", Math.round(resolutionRate * 100.0) / 100.0);
        sla.put("period", "30 days");
        return sla;
    }

    private void sendAlertEmail(Alert alert) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo("admin@healthcheck.local");
            msg.setSubject("[ALERTE] " + alert.getSeverity().toUpperCase() + " - " + alert.getAlertName());
            msg.setText(
                "Serveur : " + alert.getServerName() + "\n" +
                "Alerte  : " + alert.getAlertName() + "\n" +
                "Sévérité: " + alert.getSeverity() + "\n" +
                "Message : " + alert.getDescription() + "\n" +
                "Heure   : " + alert.getFiredAt()
            );
            mailSender.send(msg);
            log.info("Email sent for alert: {}", alert.getAlertName());
        } catch (Exception e) {
            log.warn("Email not sent: {}", e.getMessage());
        }
    }
}
