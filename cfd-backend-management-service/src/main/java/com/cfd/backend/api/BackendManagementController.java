package com.cfd.backend.api;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cfd.backend.service.BackendDashboardService;

@RestController
@RequestMapping("/backend")
public class BackendManagementController {

    private final BackendDashboardService backendDashboardService;

    public BackendManagementController(BackendDashboardService backendDashboardService) {
        this.backendDashboardService = backendDashboardService;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return backendDashboardService.metricsSnapshot();
    }
}
