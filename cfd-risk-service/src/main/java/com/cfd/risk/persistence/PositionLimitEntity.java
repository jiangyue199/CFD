package com.cfd.risk.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 持仓限额实体类。
 *
 * <p>对应数据库 position_limit 表，定义各品种的持仓限额和全局风险敞口限制。
 * perSymbolLimit 为单一品种净持仓限额，globalNetExposure 为全局风险敞口限额。</p>
 *
 * @author CFD Platform Team
 */
@TableName("position_limit")
public class PositionLimitEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("symbol")
    private String symbol;

    @TableField("per_symbol_limit")
    private BigDecimal perSymbolLimit;

    @TableField("global_net_exposure")
    private BigDecimal globalNetExposure;

    @TableField("currency")
    private String currency;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public BigDecimal getPerSymbolLimit() { return perSymbolLimit; }
    public void setPerSymbolLimit(BigDecimal perSymbolLimit) { this.perSymbolLimit = perSymbolLimit; }
    public BigDecimal getGlobalNetExposure() { return globalNetExposure; }
    public void setGlobalNetExposure(BigDecimal globalNetExposure) { this.globalNetExposure = globalNetExposure; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
