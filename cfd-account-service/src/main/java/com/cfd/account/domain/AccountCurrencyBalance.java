package com.cfd.account.domain;

import java.math.BigDecimal;

/**
 * 账户币种余额领域模型。
 *
 * <p>表示某一用户在指定币种下的可用余额，支持入金（增加余额）和出金（扣减余额）操作。
 * 主键由 {@code userId} 和 {@code currency} 组合而成。</p>
 *
 * @author CFD Platform Team
 */
public class AccountCurrencyBalance {

    /** 用户ID */
    private final String userId;
    /** 币种代码 */
    private final String currency;
    /** 组合主键（userId:currency） */
    private final String pk;
    /** 可用余额 */
    private BigDecimal available;

    /**
     * 构造账户币种余额对象。
     *
     * @param userId    用户ID
     * @param currency  币种代码
     * @param available 初始可用余额
     */
    public AccountCurrencyBalance(String userId, String currency, BigDecimal available) {
        this.userId = userId;
        this.currency = currency;
        this.pk = userId + ":" + currency;
        this.available = available;
    }

    /**
     * 获取用户ID。
     *
     * @return 用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 获取币种代码。
     *
     * @return 币种代码
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * 获取组合主键。
     *
     * @return 组合主键字符串
     */
    public String getPk() {
        return pk;
    }

    /**
     * 获取可用余额。
     *
     * @return 可用余额
     */
    public BigDecimal getAvailable() {
        return available;
    }

    /**
     * 入金操作，增加可用余额。
     *
     * @param amount 入金金额
     */
    public void deposit(BigDecimal amount) {
        this.available = this.available.add(amount);
    }

    /**
     * 出金操作，扣减可用余额。
     *
     * @param amount 出金金额
     * @throws IllegalStateException 当余额不足时抛出
     */
    public void withdraw(BigDecimal amount) {
        if (available.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        this.available = this.available.subtract(amount);
    }
}
