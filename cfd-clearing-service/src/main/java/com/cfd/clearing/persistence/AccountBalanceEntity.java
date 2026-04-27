package com.cfd.clearing.persistence;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("clearing_account_balance")
public class AccountBalanceEntity {

    @TableId(type = IdType.INPUT)
    private String userId;
    private BigDecimal available;
    private BigDecimal frozenMargin;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public void setAvailable(BigDecimal available) {
        this.available = available;
    }

    public BigDecimal getFrozenMargin() {
        return frozenMargin;
    }

    public void setFrozenMargin(BigDecimal frozenMargin) {
        this.frozenMargin = frozenMargin;
    }
}
