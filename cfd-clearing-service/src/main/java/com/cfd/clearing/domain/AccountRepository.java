package com.cfd.clearing.domain;

import java.util.Optional;

public interface AccountRepository {

    AccountBalance saveIfAbsent(AccountBalance accountBalance);

    Optional<AccountBalance> findByUserId(String userId);

    void update(AccountBalance accountBalance);
}
