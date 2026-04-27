package com.cfd.clearing.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cfd.clearing.domain.AccountBalance;
import com.cfd.clearing.domain.AccountRepository;

@Repository
public class MybatisPlusAccountRepository implements AccountRepository {

    private final AccountBalanceDbMapper mapper;

    public MybatisPlusAccountRepository(AccountBalanceDbMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public AccountBalance saveIfAbsent(AccountBalance accountBalance) {
        AccountBalanceEntity existing = mapper.selectById(accountBalance.getUserId());
        if (existing != null) {
            return toDomain(existing);
        }
        mapper.insert(toEntity(accountBalance));
        return accountBalance;
    }

    @Override
    public Optional<AccountBalance> findByUserId(String userId) {
        return Optional.ofNullable(mapper.selectById(userId)).map(this::toDomain);
    }

    @Override
    public void update(AccountBalance accountBalance) {
        mapper.updateById(toEntity(accountBalance));
    }

    private AccountBalance toDomain(AccountBalanceEntity entity) {
        return new AccountBalance(entity.getUserId(), entity.getAvailable(), entity.getFrozenMargin());
    }

    private AccountBalanceEntity toEntity(AccountBalance accountBalance) {
        AccountBalanceEntity entity = new AccountBalanceEntity();
        entity.setUserId(accountBalance.getUserId());
        entity.setAvailable(accountBalance.getAvailable());
        entity.setFrozenMargin(accountBalance.getFrozenMargin());
        return entity;
    }
}
