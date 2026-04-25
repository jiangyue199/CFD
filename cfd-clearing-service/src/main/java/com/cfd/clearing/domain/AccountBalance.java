package com.cfd.clearing.domain;

import java.math.BigDecimal;

public class AccountBalance {

    private final String userId;
    private BigDecimal available;
    private BigDecimal frozenMargin;

    public AccountBalance(String userId, BigDecimal available, BigDecimal frozenMargin) {
        this.userId = userId;
        this.available = available;
        this.frozenMargin = frozenMargin;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public BigDecimal getFrozenMargin() {
        return frozenMargin;
    }

    public void debitMargin(BigDecimal margin) {
        this.available = this.available.subtract(margin);
        this.frozenMargin = this.frozenMargin.add(margin);
    }
}
