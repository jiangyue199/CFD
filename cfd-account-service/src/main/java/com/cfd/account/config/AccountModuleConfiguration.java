package com.cfd.account.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cfd.account.domain.AccountBalanceRepository;
import com.cfd.account.persistence.MybatisPlusAccountBalanceRepository;

@Configuration
public class AccountModuleConfiguration {

    @Bean
    public AccountBalanceRepository accountBalanceRepository(MybatisPlusAccountBalanceRepository repository) {
        return repository;
    }
}
