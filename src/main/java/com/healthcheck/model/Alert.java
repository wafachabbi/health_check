package com.healthcheck.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Data
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String serverName;
    private String alertName;
    private String severity;
    private String status;
    private String description;
    private LocalDateTime firedAt;
    private LocalDateTime resolvedAt;
}
