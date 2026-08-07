package com.healthcheck.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String action;
    private String resource;
    private String details;
    private String ipAddress;
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() { timestamp = LocalDateTime.now(); }
}
