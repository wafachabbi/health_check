package com.healthcheck.repository;

import com.healthcheck.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByStatus(String status);
    List<Alert> findByServerName(String serverName);
    List<Alert> findBySeverity(String severity);

    @Query("SELECT a FROM Alert a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:severity IS NULL OR a.severity = :severity) AND " +
           "(:serverName IS NULL OR a.serverName = :serverName)")
    List<Alert> findWithFilters(
        @Param("status") String status,
        @Param("severity") String severity,
        @Param("serverName") String serverName
    );

    // Tendances : nombre d'alertes par jour sur une période
    @Query("SELECT CAST(a.firedAt AS date), COUNT(a) FROM Alert a " +
           "WHERE a.firedAt >= :from GROUP BY CAST(a.firedAt AS date) ORDER BY CAST(a.firedAt AS date)")
    List<Object[]> countAlertsByDay(@Param("from") LocalDateTime from);

    // Taux de résolution
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.status = 'RESOLVED' AND a.firedAt >= :from")
    long countResolvedSince(@Param("from") LocalDateTime from);

    @Query("SELECT COUNT(a) FROM Alert a WHERE a.firedAt >= :from")
    long countTotalSince(@Param("from") LocalDateTime from);

    // Alertes anciennes pour purge
    List<Alert> findByFiredAtBefore(LocalDateTime date);
}
