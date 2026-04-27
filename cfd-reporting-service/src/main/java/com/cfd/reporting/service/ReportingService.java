package com.cfd.reporting.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
public class ReportingService {

    public DailyReport generateDailySummary(String businessDate) {
        return new DailyReport(businessDate, "CFD_DAILY_SUMMARY", Instant.now(), "Report generated");
    }

    public record DailyReport(String businessDate, String reportType, Instant generatedAt, String message) {}
}
