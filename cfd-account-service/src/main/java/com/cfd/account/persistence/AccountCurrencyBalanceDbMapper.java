package com.cfd.account.persistence;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 账户币种余额数据库 Mapper 接口。
 *
 * <p>继承 MyBatis-Plus {@link BaseMapper}，提供对 {@link AccountCurrencyBalanceEntity} 的基本 CRUD 操作。</p>
 *
 * @author CFD Platform Team
 */
@Mapper
public interface AccountCurrencyBalanceDbMapper extends BaseMapper<AccountCurrencyBalanceEntity> {
}
