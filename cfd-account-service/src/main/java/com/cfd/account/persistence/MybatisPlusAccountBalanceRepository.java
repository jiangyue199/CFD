package com.cfd.account.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.account.domain.AccountBalanceRepository;
import com.cfd.account.domain.AccountCurrencyBalance;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 基于 MyBatis-Plus 的账户余额仓储实现。
 *
 * <p>通过 {@link AccountCurrencyBalanceDbMapper} 与数据库交互，
 * 实现账户余额的持久化存储和查询。</p>
 *
 * @author CFD Platform Team
 */
@Repository
public class MybatisPlusAccountBalanceRepository implements AccountBalanceRepository {

    private final AccountCurrencyBalanceDbMapper mapper;

    /**
     * 构造 MyBatis-Plus 账户余额仓储。
     *
     * @param mapper 账户币种余额数据库 Mapper
     */
    public MybatisPlusAccountBalanceRepository(AccountCurrencyBalanceDbMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     *
     * <p>先尝试更新，若记录不存在则插入新记录。</p>
     */
    @Override
    public AccountCurrencyBalance save(AccountCurrencyBalance balance) {
        AccountCurrencyBalanceEntity entity = toEntity(balance);
        int updated = mapper.updateById(entity);
        if (updated == 0) {
            mapper.insert(entity);
        }
        return balance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AccountCurrencyBalance> find(String userId, String currency) {
        LambdaQueryWrapper<AccountCurrencyBalanceEntity> query = new LambdaQueryWrapper<AccountCurrencyBalanceEntity>()
                .eq(AccountCurrencyBalanceEntity::getUserId, userId)
                .eq(AccountCurrencyBalanceEntity::getCurrency, currency)
                .last("limit 1");
        return Optional.ofNullable(mapper.selectOne(query)).map(this::toDomain);
    }

    /**
     * 将领域模型转换为数据库实体。
     *
     * @param balance 账户余额领域对象
     * @return 数据库实体
     */
    private AccountCurrencyBalanceEntity toEntity(AccountCurrencyBalance balance) {
        AccountCurrencyBalanceEntity entity = new AccountCurrencyBalanceEntity();
        entity.setPk(balance.getUserId() + ":" + balance.getCurrency());
        entity.setUserId(balance.getUserId());
        entity.setCurrency(balance.getCurrency());
        entity.setAvailable(balance.getAvailable());
        return entity;
    }

    /**
     * 将数据库实体转换为领域模型。
     *
     * @param entity 数据库实体
     * @return 账户余额领域对象
     */
    private AccountCurrencyBalance toDomain(AccountCurrencyBalanceEntity entity) {
        return new AccountCurrencyBalance(entity.getUserId(), entity.getCurrency(), entity.getAvailable());
    }
}
