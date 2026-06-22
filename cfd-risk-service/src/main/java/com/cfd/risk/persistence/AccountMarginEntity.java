package com.cfd.risk.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户保证金快照实体类。
 *
 * <p>对应数据库 account_margin 表，存储用户账户的实时保证金快照：
 * <ul>
 *   <li>balance: 账户余额</li>
 *   <li>equity: 账户权益（余额+浮动盈亏）</li>
 *   <li>usedMargin: 已用保证金</li>
 *   <li>freeMargin: 可用保证金</li>
 *   <li>marginLevel: 保证金水平(%)</li>
 *   <li>floatingPnl: 浮动盈亏</li>
 * </ul>
 * </p>
 *
 * @author CFD Platform Team
 */
@TableName("account_margin")
public class AccountMarginEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private String userId;

    @TableField("balance")
    private BigDecimal balance;

    @TableField("equity")
    private BigDecimal equity;

    @TableField("used_margin")
    private BigDecimal usedMargin;

    @TableField("free_margin")
    private BigDecimal freeMargin;

    @TableField("margin_level")
    private BigDecimal marginLevel;

    @TableField("floating_pnl")
    private BigDecimal floatingPnl;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public BigDecimal getEquity() { return equity; }
    public void setEquity(BigDecimal equity) { this.equity = equity; }
    public BigDecimal getUsedMargin() { return usedMargin; }
    public void setUsedMargin(BigDecimal usedMargin) { this.usedMargin = usedMargin; }
    public BigDecimal getFreeMargin() { return freeMargin; }
    public void setFreeMargin(BigDecimal freeMargin) { this.freeMargin = freeMargin; }
    public BigDecimal getMarginLevel() { return marginLevel; }
    public void setMarginLevel(BigDecimal marginLevel) { this.marginLevel = marginLevel; }
    public BigDecimal getFloatingPnl() { return floatingPnl; }
    public void setFloatingPnl(BigDecimal floatingPnl) { this.floatingPnl = floatingPnl; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
