package com.cfd.clearing.domain;

import java.util.Optional;

/**
 * 账户仓储接口。
 *
 * <p>定义账户余额聚合根的持久化操作契约，包括幂等保存、按用户ID查询和更新操作。</p>
 *
 * @author CFD Platform Team
 */
public interface AccountRepository {

    /**
     * 幂等保存账户余额，若已存在则返回已有记录。
     *
     * @param accountBalance 待保存的账户余额对象
     * @return 已保存或已存在的账户余额对象
     */
    AccountBalance saveIfAbsent(AccountBalance accountBalance);

    /**
     * 根据用户ID查询账户余额。
     *
     * @param userId 用户ID
     * @return 账户余额对象（如果存在）
     */
    Optional<AccountBalance> findByUserId(String userId);

    /**
     * 更新账户余额。
     *
     * @param accountBalance 待更新的账户余额对象
     */
    void update(AccountBalance accountBalance);
}
