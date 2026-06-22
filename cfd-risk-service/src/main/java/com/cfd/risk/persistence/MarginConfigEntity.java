package com.cfd.risk.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 保证金配置实体类。
 *
 * <p>对应数据库 margin_config 表，定义不同用户等级的保证金相关配置：
 * <ul>
 *   <li>marginCallLevel: 保证金预警线（规则2.2）</li>
 *   <li>stopOutLevel: 强平线（规则2.3）</li>
 *   <li>negativeBalanceProtection: 负余额保护（规则2.4）</li>
 *   <li>concentrationRatio: 品种集中度阈值（规则2.5）</li>
 *   <li>floatingLossRatio: 浮动亏损比例阈值（规则2.6）</li>
 * </ul>
 * </p>
 *
 * @author CFD Platform Team
 */
@TableName("margin_config")
public class MarginConfigEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_level")
    private String userLevel;

    @TableField("margin_call_level")
    private BigDecimal marginCallLevel;

    @TableField("stop_out_level")
    private BigDecimal stopOutLevel;

    @TableField("negative_balance_protection")
    private Boolean negativeBalanceProtection;

    @TableField("concentration_ratio")
    private BigDecimal concentrationRatio;

    @TableField("floating_loss_ratio")
    private BigDecimal floatingLossRatio;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserLevel() { return userLevel; }
    public void setUserLevel(String userLevel) { this.userLevel = userLevel; }
    public BigDecimal getMarginCallLevel() { return marginCallLevel; }
    public void setMarginCallLevel(BigDecimal marginCallLevel) { this.marginCallLevel = marginCallLevel; }
    public BigDecimal getStopOutLevel() { return stopOutLevel; }
    public void setStopOutLevel(BigDecimal stopOutLevel) { this.stopOutLevel = stopOutLevel; }
    public Boolean getNegativeBalanceProtection() { return negativeBalanceProtection; }
    public void setNegativeBalanceProtection(Boolean negativeBalanceProtection) { this.negativeBalanceProtection = negativeBalanceProtection; }
    public BigDecimal getConcentrationRatio() { return concentrationRatio; }
    public void setConcentrationRatio(BigDecimal concentrationRatio) { this.concentrationRatio = concentrationRatio; }
    public BigDecimal getFloatingLossRatio() { return floatingLossRatio; }
    public void setFloatingLossRatio(BigDecimal floatingLossRatio) { this.floatingLossRatio = floatingLossRatio; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
