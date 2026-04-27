package com.cfd.account.domain;

import java.util.Optional;

public interface AccountBalanceRepository {

    AccountCurrencyBalance save(AccountCurrencyBalance balance);

    Optional<AccountCurrencyBalance> find(String userId, String currency);
}
