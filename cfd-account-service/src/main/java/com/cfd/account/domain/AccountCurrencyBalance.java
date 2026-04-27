package com.cfd.account.domain;

import java.math.BigDecimal;

public class AccountCurrencyBalance {

    private final String userId;
    private final String currency;
    private BigDecimal available;

    public AccountCurrencyBalance(String userId, String currency, BigDecimal available) {
        this.userId = userId;
        this.currency = currency;
        this.available = available;
    }

    public String getUserId() {
        return userId;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public void deposit(BigDecimal amount) {
        this.available = this.available.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (available.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        this.available = this.available.subtract(amount);
    }
}
