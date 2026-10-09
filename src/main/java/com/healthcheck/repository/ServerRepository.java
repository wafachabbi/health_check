package com.healthcheck.repository;

import com.healthcheck.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ServerRepository extends JpaRepository<Server, Long> {
    List<Server> findByStatus(String status);
    List<Server> findByEnvironment(String environment);
    List<Server> findBySite(String site);
    List<Server> findByBay(String bay);

    @Query("SELECT s FROM Server s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:environment IS NULL OR s.environment = :environment) AND " +
           "(:site IS NULL OR s.site = :site) AND " +
           "(:bay IS NULL OR s.bay = :bay)")
    List<Server> findWithFilters(
        @Param("status") String status,
        @Param("environment") String environment,
        @Param("site") String site,
        @Param("bay") String bay
    );

    // Conformité firmware : serveurs dont le firmware != baseline
    @Query("SELECT s FROM Server s WHERE s.baselineFirmware IS NOT NULL AND s.firmwareVersion != s.baselineFirmware")
    List<Server> findNonCompliantFirmware();
}
