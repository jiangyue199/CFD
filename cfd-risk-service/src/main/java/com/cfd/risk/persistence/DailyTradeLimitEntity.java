package com.cfd.risk.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 日交易限额实体类。
 *
 * <p>对应数据库 daily_trade_limit 表，定义用户每日交易限额。
 * symbol 为 "*" 表示全局限额，否则为品种特定限额。</p>
 *
 * @author CFD Platform Team
 */
@TableName("daily_trade_limit")
public class DailyTradeLimitEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private String userId;

    @TableField("symbol")
    private String symbol;

    @TableField("daily_max_lots")
    private BigDecimal dailyMaxLots;

    @TableField("daily_max_orders")
    private Integer dailyMaxOrders;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public BigDecimal getDailyMaxLots() { return dailyMaxLots; }
    public void setDailyMaxLots(BigDecimal dailyMaxLots) { this.dailyMaxLots = dailyMaxLots; }
    public Integer getDailyMaxOrders() { return dailyMaxOrders; }
    public void setDailyMaxOrders(Integer dailyMaxOrders) { this.dailyMaxOrders = dailyMaxOrders; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
