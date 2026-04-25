package com.cfd.reporting.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cfd.reporting.service.ReportingService;

@RestController
@RequestMapping("/reports")
public class ReportingController {

    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/daily/{businessDate}")
    public ReportingService.DailyReport generateDaily(@PathVariable String businessDate) {
        return reportingService.generateDailySummary(businessDate);
    }
}
