package com.cfd.backend.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class BackendDashboardService {

    public Map<String, Object> metricsSnapshot() {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "onlineUsers", 1024,
                "openOrders", 382,
                "openPositions", 147,
                "riskAlerts", 2
        );
    }
}
