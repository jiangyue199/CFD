package com.cfd.reporting.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.reporting.persistence.DailyReportDbMapper;
import com.cfd.reporting.persistence.DailyReportEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * 报表服务。
 *
 * <p>封装报表生成的业务逻辑，支持按营业日期生成或获取 CFD 日报摘要。</p>
 *
 * @author CFD Platform Team
 */
@Service
public class ReportingService {

    private final DailyReportDbMapper dailyReportDbMapper;

    /**
     * 构造报表服务。
     *
     * @param dailyReportDbMapper 日报数据库 Mapper
     */
    public ReportingService(DailyReportDbMapper dailyReportDbMapper) {
        this.dailyReportDbMapper = dailyReportDbMapper;
    }

    /**
     * 生成或获取指定营业日的日报摘要。
     *
     * <p>若该日期已存在报表则直接返回，否则生成新报表并持久化。</p>
     *
     * @param businessDate 营业日期
     * @return 日报信息
     */
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

    /**
     * 日报记录。
     *
     * @param businessDate 营业日期
     * @param reportType   报表类型
     * @param generatedAt  生成时间
     * @param message      报表摘要信息
     */
    public record DailyReport(String businessDate, String reportType, Instant generatedAt, String message) {}
}
