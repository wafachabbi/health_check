package com.healthcheck.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;
import java.util.HashMap;

@Service
@Slf4j
public class RedfishService {

    @Value("${redfish.username:admin}")
    private String username;

    @Value("${redfish.password:admin}")
    private String password;

    public Map<String, Object> getServerHealth(String iloIp) {
        try {
            WebClient client = WebClient.builder()
                .baseUrl("https://" + iloIp)
                .defaultHeaders(h -> h.setBasicAuth(username, password))
                .build();

            Map<String, Object> result = client.get()
                .uri("/redfish/v1/Systems/1")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            return result != null ? result : mockData(iloIp);
        } catch (Exception e) {
            log.warn("Redfish not reachable for {}: {}", iloIp, e.getMessage());
            return mockData(iloIp);
        }
    }

    private Map<String, Object> mockData(String iloIp) {
        Map<String, Object> mock = new HashMap<>();
        mock.put("iloIp", iloIp);
        mock.put("status", "Mock - iLO not reachable yet");
        mock.put("health", "OK");
        mock.put("model", "HPE ProLiant DL380 Gen10");
        mock.put("powerState", "On");
        mock.put("cpuHealth", "OK");
        mock.put("memoryHealth", "OK");
        mock.put("storageHealth", "OK");
        return mock;
    }
}
