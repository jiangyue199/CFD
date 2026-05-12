package com.cfd.backend.api;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cfd.backend.service.BackendDashboardService;

/**
 * 后台管理 REST 控制器。
 *
 * <p>提供管理仪表盘数据查询的 HTTP 接口，
 * 所有请求路径以 {@code /backend} 为前缀。</p>
 *
 * @author CFD Platform Team
 */
@RestController
@RequestMapping("/backend")
public class BackendManagementController {

    private final BackendDashboardService backendDashboardService;

    /**
     * 构造后台管理控制器。
     *
     * @param backendDashboardService 后台仪表盘服务
     */
    public BackendManagementController(BackendDashboardService backendDashboardService) {
        this.backendDashboardService = backendDashboardService;
    }

    /**
     * 获取仪表盘运营指标快照。
     *
     * @return 包含各项运营指标的键值对映射
     */
    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return backendDashboardService.metricsSnapshot();
    }
}
