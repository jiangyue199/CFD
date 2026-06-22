package com.cfd.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.backend.persistence.DashboardMetricDbMapper;
import com.cfd.backend.persistence.DashboardMetricEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * 后台仪表盘服务。
 *
 * <p>封装后台管理仪表盘的业务逻辑，提供运营指标的快照查询。
 * 查询时自动确保默认指标存在。</p>
 *
 * @author CFD Platform Team
 */
@Service
public class BackendDashboardService {

    private final DashboardMetricDbMapper dashboardMetricDbMapper;

    /**
     * 构造后台仪表盘服务。
     *
     * @param dashboardMetricDbMapper 仪表盘指标数据库 Mapper
     */
    public BackendDashboardService(DashboardMetricDbMapper dashboardMetricDbMapper) {
        this.dashboardMetricDbMapper = dashboardMetricDbMapper;
    }

    /**
     * 获取运营指标快照。
     *
     * <p>确保默认指标存在后，从数据库加载全部指标并附加当前时间戳。</p>
     *
     * @return 包含各项运营指标和时间戳的映射
     */
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

    /**
     * 若指定键的指标不存在，则插入默认值。
     *
     * @param key   指标键
     * @param value 默认指标值
     */
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
