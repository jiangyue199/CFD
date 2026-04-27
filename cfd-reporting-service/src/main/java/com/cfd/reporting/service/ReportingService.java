package com.cfd.reporting.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.reporting.persistence.DailyReportDbMapper;
import com.cfd.reporting.persistence.DailyReportEntity;

@Service
public class ReportingService {

    private final DailyReportDbMapper dailyReportDbMapper;

    public ReportingService(DailyReportDbMapper dailyReportDbMapper) {
        this.dailyReportDbMapper = dailyReportDbMapper;
    }

    public DailyReport generateDailySummary(String businessDate) {
        DailyReportEntity existing = dailyReportDbMapper.selectOne(new LambdaQueryWrapper<DailyReportEntity>()
                .eq(DailyReportEntity::getBusinessDate, businessDate)
                .last("limit 1"));
        if (existing != null) {
            return new DailyReport(
                    existing.getBusinessDate(),
                    existing.getReportType(),
                    existing.getGeneratedAt(),
                    existing.getMessage());
        }

        Instant generatedAt = Instant.now();
        DailyReportEntity entity = new DailyReportEntity();
        entity.setBusinessDate(businessDate);
        entity.setReportType("CFD_DAILY_SUMMARY");
        entity.setGeneratedAt(generatedAt);
        entity.setMessage("Report generated");
        dailyReportDbMapper.insert(entity);
        return new DailyReport(entity.getBusinessDate(), entity.getReportType(), generatedAt, entity.getMessage());
    }

    public record DailyReport(String businessDate, String reportType, Instant generatedAt, String message) {}
}
