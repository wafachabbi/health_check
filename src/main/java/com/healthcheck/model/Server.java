package com.healthcheck.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "servers")
@Data
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String ipAddress;
    private String iloIp;
    private String model;
    private String serialNumber;
    private String location;
    private String site;
    private String bay;
    private String environment; // PRODUCTION, PREPROD, DEV, TEST
    private String contactReferent;
    private String firmwareVersion;
    private String baselineFirmware;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
