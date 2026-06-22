package com.cfd.account.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

/**
 * 账户币种余额数据库实体。
 *
 * <p>映射数据库表 {@code account_currency_balance}，
 * 存储用户在特定币种下的可用余额信息。</p>
 *
 * @author CFD Platform Team
 */
@TableName("account_currency_balance")
public class AccountCurrencyBalanceEntity {

    /** 组合主键（userId:currency） */
    @TableId(value = "pk", type = IdType.INPUT)
    private String pk;

    /** 用户ID */
    @TableField("user_id")
    private String userId;

    /** 币种代码 */
    @TableField("currency")
    private String currency;

    /** 可用余额 */
    @TableField("available")
    private BigDecimal available;

    /** @return 组合主键 */
    public String getPk() {
        return pk;
    }

    /** @param pk 组合主键 */
    public void setPk(String pk) {
        this.pk = pk;
    }

    /** @return 用户ID */
    public String getUserId() {
        return userId;
    }

    /** @param userId 用户ID */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** @return 币种代码 */
    public String getCurrency() {
        return currency;
    }

    /** @param currency 币种代码 */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /** @return 可用余额 */
    public BigDecimal getAvailable() {
        return available;
    }

    /** @param available 可用余额 */
    public void setAvailable(BigDecimal available) {
        this.available = available;
    }
}
