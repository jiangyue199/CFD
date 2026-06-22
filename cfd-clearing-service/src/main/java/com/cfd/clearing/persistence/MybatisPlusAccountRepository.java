package com.cfd.clearing.persistence;

import com.cfd.clearing.domain.AccountBalance;
import com.cfd.clearing.domain.AccountRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 基于MyBatis-Plus的账户仓储实现。
 *
 * <p>使用MySQL数据库持久化账户余额数据，实现{@link AccountRepository}接口。
 * 提供幂等保存、按用户ID查询及更新功能。</p>
 *
 * @author CFD Platform Team
 */
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
