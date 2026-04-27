package com.cfd.backend.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.backend.persistence.DashboardMetricDbMapper;
import com.cfd.backend.persistence.DashboardMetricEntity;

@Service
public class BackendDashboardService {

    private final DashboardMetricDbMapper dashboardMetricDbMapper;

    public BackendDashboardService(DashboardMetricDbMapper dashboardMetricDbMapper) {
        this.dashboardMetricDbMapper = dashboardMetricDbMapper;
    }

    @Transactional
    public Map<String, Object> metricsSnapshot() {
        ensureMetric("onlineUsers", "1024");
        ensureMetric("openOrders", "382");
        ensureMetric("openPositions", "147");
        ensureMetric("riskAlerts", "2");

        Map<String, Object> metrics = new HashMap<>();
        dashboardMetricDbMapper.selectList(new LambdaQueryWrapper<DashboardMetricEntity>())
                .forEach(item -> metrics.put(item.getMetricKey(), item.getMetricValue()));
        metrics.put("timestamp", Instant.now().toString());
        return metrics;
    }

    private void ensureMetric(String key, String value) {
        if (dashboardMetricDbMapper.selectById(key) != null) {
            return;
        }
        DashboardMetricEntity entity = new DashboardMetricEntity();
        entity.setMetricKey(key);
        entity.setMetricValue(value);
        dashboardMetricDbMapper.insert(entity);
    }
}
