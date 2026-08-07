package com.healthcheck.repository;

import com.healthcheck.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServerRepository extends JpaRepository<Server, Long> {
    List<Server> findByStatus(String status);
}
