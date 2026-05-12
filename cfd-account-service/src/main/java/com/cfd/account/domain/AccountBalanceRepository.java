package com.cfd.account.domain;

import java.util.Optional;

/**
 * 账户余额仓储接口。
 *
 * <p>定义账户币种余额的持久化操作契约，包括保存和查询。</p>
 *
 * @author CFD Platform Team
 */
public interface AccountBalanceRepository {

    /**
     * 保存或更新账户币种余额。
     *
     * @param balance 待保存的账户余额对象
     * @return 保存后的账户余额对象
     */
    AccountCurrencyBalance save(AccountCurrencyBalance balance);

    /**
     * 根据用户ID和币种查询账户余额。
     *
     * @param userId   用户ID
     * @param currency 币种代码
     * @return 包含账户余额的 Optional，不存在时为空
     */
    Optional<AccountCurrencyBalance> find(String userId, String currency);
}
