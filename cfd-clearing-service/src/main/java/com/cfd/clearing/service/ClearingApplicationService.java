package com.cfd.clearing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cfd.clearing.domain.AccountBalance;
import com.cfd.clearing.domain.AccountRepository;
import com.cfd.domain.model.AccountBalanceResponse;
import com.cfd.domain.model.TradeOpenedEvent;

/**
 * 清算应用服务。
 *
 * <p>处理交易开仓事件的清算逻辑：查找或创建用户账户，校验可用余额是否充足，
 * 执行保证金扣减（从可用余额扣除并计入冻结保证金）。</p>
 *
 * @author CFD Platform Team
 */
@Service
public class ClearingApplicationService {

    private final AccountRepository accountRepository;

    public ClearingApplicationService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * 处理交易开仓事件。
     *
     * <p>执行以下步骤：
     * <ol>
     *   <li>查找用户账户，不存在则创建默认账户（可用余额100000）</li>
     *   <li>校验可用余额是否满足保证金需求</li>
     *   <li>执行保证金扣减：扣减可用余额，增加冻结保证金</li>
     *   <li>持久化更新后的账户余额</li>
     * </ol>
     * </p>
     *
     * @param event 交易开仓事件
     * @throws IllegalStateException 当可用余额不足时抛出
     */
    @Transactional
    public synchronized void handleTradeOpened(TradeOpenedEvent event) {
        AccountBalance account = accountRepository.findByUserId(event.userId())
                .orElseGet(() -> accountRepository.saveIfAbsent(new AccountBalance(event.userId(), new BigDecimal("100000.00000000"), BigDecimal.ZERO)));

        if (account.getAvailable().compareTo(event.margin()) < 0) {
            throw new IllegalStateException("Insufficient margin");
        }

        account.debitMargin(event.margin());
        accountRepository.update(account);
    }

    /**
     * 查询用户账户余额。
     *
     * <p>若用户账户不存在，则自动创建默认账户并返回。</p>
     *
     * @param userId 用户ID
     * @return 账户余额响应DTO
     */
    public AccountBalanceResponse queryAccount(String userId) {
        AccountBalance account = accountRepository.findByUserId(userId)
                .orElseGet(() -> accountRepository.saveIfAbsent(new AccountBalance(userId, new BigDecimal("100000.00000000"), BigDecimal.ZERO)));
        return new AccountBalanceResponse(
                account.getUserId(),
                account.getAvailable().setScale(8, RoundingMode.HALF_UP),
                account.getFrozenMargin().setScale(8, RoundingMode.HALF_UP));
    }
}
