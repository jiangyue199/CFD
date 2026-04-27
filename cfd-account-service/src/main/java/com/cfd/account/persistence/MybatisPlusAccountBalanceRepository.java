package com.cfd.account.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.account.domain.AccountBalanceRepository;
import com.cfd.account.domain.AccountCurrencyBalance;

@Repository
public class MybatisPlusAccountBalanceRepository implements AccountBalanceRepository {

    private final AccountCurrencyBalanceDbMapper mapper;

    public MybatisPlusAccountBalanceRepository(AccountCurrencyBalanceDbMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public AccountCurrencyBalance save(AccountCurrencyBalance balance) {
        AccountCurrencyBalanceEntity entity = toEntity(balance);
        int updated = mapper.updateById(entity);
        if (updated == 0) {
            mapper.insert(entity);
        }
        return balance;
    }

    @Override
    public Optional<AccountCurrencyBalance> find(String userId, String currency) {
        LambdaQueryWrapper<AccountCurrencyBalanceEntity> query = new LambdaQueryWrapper<AccountCurrencyBalanceEntity>()
                .eq(AccountCurrencyBalanceEntity::getUserId, userId)
                .eq(AccountCurrencyBalanceEntity::getCurrency, currency)
                .last("limit 1");
        return Optional.ofNullable(mapper.selectOne(query)).map(this::toDomain);
    }

    private AccountCurrencyBalanceEntity toEntity(AccountCurrencyBalance balance) {
        AccountCurrencyBalanceEntity entity = new AccountCurrencyBalanceEntity();
        entity.setPk(balance.getUserId() + ":" + balance.getCurrency());
        entity.setUserId(balance.getUserId());
        entity.setCurrency(balance.getCurrency());
        entity.setAvailable(balance.getAvailable());
        return entity;
    }

    private AccountCurrencyBalance toDomain(AccountCurrencyBalanceEntity entity) {
        return new AccountCurrencyBalance(entity.getUserId(), entity.getCurrency(), entity.getAvailable());
    }
}
