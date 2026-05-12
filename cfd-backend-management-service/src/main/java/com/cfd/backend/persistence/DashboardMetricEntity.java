package com.cfd.backend.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 仪表盘指标数据库实体。
 *
 * <p>映射数据库表 {@code dashboard_metric}，存储后台运营指标的键值对数据。</p>
 *
 * @author CFD Platform Team
 */
@TableName("dashboard_metric")
public class DashboardMetricEntity {

    /** 指标键（主键） */
    @TableId(value = "metric_key", type = IdType.INPUT)
    private String metricKey;

    /** 指标值 */
    @TableField("metric_value")
    private String metricValue;

    /** @return 指标键 */
    public String getMetricKey() {
        return metricKey;
    }

    /** @param metricKey 指标键 */
    public void setMetricKey(String metricKey) {
        this.metricKey = metricKey;
    }

    /** @return 指标值 */
    public String getMetricValue() {
        return metricValue;
    }

    /** @param metricValue 指标值 */
    public void setMetricValue(String metricValue) {
        this.metricValue = metricValue;
    }
}
