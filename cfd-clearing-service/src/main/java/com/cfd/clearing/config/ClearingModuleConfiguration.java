package com.cfd.clearing.config;

import java.math.BigDecimal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cfd.clearing.domain.AccountBalance;
import com.cfd.clearing.domain.AccountRepository;
import com.cfd.clearing.domain.InMemoryAccountRepository;

@Configuration
public class ClearingModuleConfiguration {

    @Bean
    public AccountRepository accountRepository() {
        InMemoryAccountRepository repository = new InMemoryAccountRepository();
        repository.saveIfAbsent(new AccountBalance("demo-user", new BigDecimal("100000.00000000"), BigDecimal.ZERO));
        return repository;
    }
}
