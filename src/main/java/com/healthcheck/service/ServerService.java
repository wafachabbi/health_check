package com.healthcheck.service;

import com.healthcheck.model.Server;
import com.healthcheck.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServerService {

    private final ServerRepository serverRepository;
    private final AuditService auditService;

    public List<Server> getAll() { return serverRepository.findAll(); }

    public Server getById(Long id) {
        return serverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Server not found: " + id));
    }

    public Server create(Server server) {
        Server saved = serverRepository.save(server);
        auditService.log("system", "CREATE_SERVER", "servers", "Added server: " + saved.getName() + " (" + saved.getIpAddress() + ")", "system");
        return saved;
    }

    public Server update(Long id, Server server) {
        serverRepository.findById(id).orElseThrow(() -> new RuntimeException("Server not found"));
        server.setId(id);
        Server saved = serverRepository.save(server);
        auditService.log("system", "UPDATE_SERVER", "servers", "Updated server: " + saved.getName(), "system");
        return saved;
    }

    public void delete(Long id) {
        Server server = getById(id);
        serverRepository.deleteById(id);
        auditService.log("system", "DELETE_SERVER", "servers", "Deleted server: " + server.getName(), "system");
    }
}
