package com.cfd.clearing.domain;

import java.math.BigDecimal;

/**
 * 账户余额聚合根。
 *
 * <p>表示用户的清算账户，包含可用余额和冻结保证金。
 * 提供保证金扣减操作：从可用余额扣除并计入冻结保证金。</p>
 *
 * @author CFD Platform Team
 */
public class AccountBalance {

    private final String userId;
    private BigDecimal available;
    private BigDecimal frozenMargin;

    /**
     * 构造账户余额对象。
     *
     * @param userId 用户ID
     * @param available 可用余额
     * @param frozenMargin 冻结保证金
     */
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

    /**
     * 扣减保证金。
     *
     * <p>从可用余额中扣除指定金额，并将该金额计入冻结保证金。</p>
     *
     * @param margin 需扣减的保证金金额
     */
    public void debitMargin(BigDecimal margin) {
        this.available = this.available.subtract(margin);
        this.frozenMargin = this.frozenMargin.add(margin);
    }
}
