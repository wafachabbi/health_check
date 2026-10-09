package com.healthcheck.repository;

import com.healthcheck.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUsernameOrderByTimestampDesc(String username);
    List<AuditLog> findAllByOrderByTimestampDesc();
    List<AuditLog> findByTimestampBefore(LocalDateTime date);
}
