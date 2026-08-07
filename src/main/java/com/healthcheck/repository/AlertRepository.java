package com.healthcheck.repository;

import com.healthcheck.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByStatus(String status);
    List<Alert> findByServerName(String serverName);
}
