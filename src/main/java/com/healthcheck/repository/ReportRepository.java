package com.healthcheck.repository;

import com.healthcheck.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByOrderByGeneratedAtDesc();
    List<Report> findByTypeOrderByGeneratedAtDesc(String type);
}
