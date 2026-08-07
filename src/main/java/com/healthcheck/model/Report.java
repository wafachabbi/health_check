package com.healthcheck.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // DAILY, WEEKLY, MONTHLY
    private String filename;
    private LocalDateTime generatedAt;
    private String status; // GENERATED, FAILED
    private Integer totalServers;
    private Integer totalAlerts;
    private Integer alertsNew;
    private Integer alertsAcknowledged;
    private Integer alertsResolved;

    @PrePersist
    protected void onCreate() { generatedAt = LocalDateTime.now(); }
}
