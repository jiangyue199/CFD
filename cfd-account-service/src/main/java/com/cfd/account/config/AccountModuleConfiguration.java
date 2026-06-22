package com.cfd.account.config;

import com.cfd.account.domain.AccountBalanceRepository;
import com.cfd.account.persistence.MybatisPlusAccountBalanceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 账户模块 Spring 配置类。
 *
 * <p>负责注册账户模块相关的 Bean，将 MyBatis-Plus 仓储实现绑定到领域仓储接口。</p>
 *
 * @author CFD Platform Team
 */
@Configuration
public class AccountModuleConfiguration {

    /**
     * 注册账户余额仓储 Bean。
     *
     * @param repository MyBatis-Plus 账户余额仓储实现
     * @return 账户余额仓储接口实例
     */
    @Bean
    public AccountBalanceRepository accountBalanceRepository(MybatisPlusAccountBalanceRepository repository) {
        return repository;
    }
}
