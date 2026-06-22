package com.cfd.risk.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 品种权限实体类。
 *
 * <p>对应数据库 symbol_permission 表，定义不同用户等级和区域对各交易品种的交易权限。
 * 支持方向限制(BUY/SELL/BOTH)、锁仓限制、最小/最大交易数量、杠杆限制等。</p>
 *
 * @author CFD Platform Team
 */
@TableName("symbol_permission")
public class SymbolPermissionEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("symbol")
    private String symbol;

    @TableField("user_level")
    private String userLevel;

    @TableField("region")
    private String region;

    @TableField("allowed")
    private Boolean allowed;

    @TableField("direction")
    private String direction;

    @TableField("hedge_allowed")
    private Boolean hedgeAllowed;

    @TableField("min_lot")
    private BigDecimal minLot;

    @TableField("max_lot")
    private BigDecimal maxLot;

    @TableField("step_lot")
    private BigDecimal stepLot;

    @TableField("max_leverage")
    private BigDecimal maxLeverage;

    @TableField("min_stop_level")
    private BigDecimal minStopLevel;

    @TableField("created_at")
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getUserLevel() { return userLevel; }
    public void setUserLevel(String userLevel) { this.userLevel = userLevel; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public Boolean getAllowed() { return allowed; }
    public void setAllowed(Boolean allowed) { this.allowed = allowed; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public Boolean getHedgeAllowed() { return hedgeAllowed; }
    public void setHedgeAllowed(Boolean hedgeAllowed) { this.hedgeAllowed = hedgeAllowed; }
    public BigDecimal getMinLot() { return minLot; }
    public void setMinLot(BigDecimal minLot) { this.minLot = minLot; }
    public BigDecimal getMaxLot() { return maxLot; }
    public void setMaxLot(BigDecimal maxLot) { this.maxLot = maxLot; }
    public BigDecimal getStepLot() { return stepLot; }
    public void setStepLot(BigDecimal stepLot) { this.stepLot = stepLot; }
    public BigDecimal getMaxLeverage() { return maxLeverage; }
    public void setMaxLeverage(BigDecimal maxLeverage) { this.maxLeverage = maxLeverage; }
    public BigDecimal getMinStopLevel() { return minStopLevel; }
    public void setMinStopLevel(BigDecimal minStopLevel) { this.minStopLevel = minStopLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
