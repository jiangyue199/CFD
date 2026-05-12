package com.cfd.reporting.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cfd.reporting.service.ReportingService;

/**
 * 报表 REST 控制器。
 *
 * <p>提供日报生成等报表查询的 HTTP 接口，
 * 所有请求路径以 {@code /reports} 为前缀。</p>
 *
 * @author CFD Platform Team
 */
@RestController
@RequestMapping("/reports")
public class ReportingController {

    private final ReportingService reportingService;

    /**
     * 构造报表控制器。
     *
     * @param reportingService 报表服务
     */
    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    /**
     * 生成或获取指定营业日的日报。
     *
     * @param businessDate 营业日期（如 2024-01-15）
     * @return 日报信息
     */
    @GetMapping("/daily/{businessDate}")
    public ReportingService.DailyReport generateDaily(@PathVariable String businessDate) {
        return reportingService.generateDailySummary(businessDate);
    }
}
