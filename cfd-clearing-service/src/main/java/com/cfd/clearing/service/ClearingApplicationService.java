package com.cfd.clearing.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cfd.clearing.domain.AccountBalance;
import com.cfd.clearing.domain.AccountRepository;
import com.cfd.domain.model.TradeOpenedEvent;

@Service
public class ClearingApplicationService {

    private final AccountRepository accountRepository;

    public ClearingApplicationService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

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
}
