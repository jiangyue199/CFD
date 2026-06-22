package com.cfd.risk.engine;

/**
 * 用户状态事实 - 包含用户账户状态信息。
 *
 * <p>用于规则1.2（白名单校验）和规则1.4（账户状态冻结校验），
 * 判断用户是否在白名单中或账户是否被冻结。</p>
 *
 * @author CFD Platform Team
 */
public class UserStatusFact {

    private String userId;
    private String status;
    private boolean isFrozen;
    private String frozenReason;

    public UserStatusFact() {
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isFrozen() { return isFrozen; }
    public void setFrozen(boolean frozen) { isFrozen = frozen; }
    public String getFrozenReason() { return frozenReason; }
    public void setFrozenReason(String frozenReason) { this.frozenReason = frozenReason; }
}
