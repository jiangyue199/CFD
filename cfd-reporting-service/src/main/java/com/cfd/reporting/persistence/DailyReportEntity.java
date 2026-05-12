package com.cfd.reporting.persistence;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 日报数据库实体。
 *
 * <p>映射数据库表 {@code daily_report}，存储日报的营业日期、报表类型、生成时间和摘要信息。</p>
 *
 * @author CFD Platform Team
 */
@TableName("daily_report")
public class DailyReportEntity {

    /** 自增主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 营业日期 */
    @TableField("business_date")
    private String businessDate;

    /** 报表类型 */
    @TableField("report_type")
    private String reportType;

    /** 生成时间 */
    @TableField("generated_at")
    private Instant generatedAt;

    /** 报表摘要信息 */
    @TableField("message")
    private String message;

    /** @return 自增主键 */
    public Long getId() {
        return id;
    }

    /** @param id 自增主键 */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return 营业日期 */
    public String getBusinessDate() {
        return businessDate;
    }

    /** @param businessDate 营业日期 */
    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    /** @return 报表类型 */
    public String getReportType() {
        return reportType;
    }

    /** @param reportType 报表类型 */
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    /** @return 生成时间 */
    public Instant getGeneratedAt() {
        return generatedAt;
    }

    /** @param generatedAt 生成时间 */
    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }

    /** @return 报表摘要信息 */
    public String getMessage() {
        return message;
    }

    /** @param message 报表摘要信息 */
    public void setMessage(String message) {
        this.message = message;
    }
}
