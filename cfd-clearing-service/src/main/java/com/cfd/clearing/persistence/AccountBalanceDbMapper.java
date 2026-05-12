package com.cfd.clearing.persistence;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 账户余额数据库Mapper接口。
 *
 * <p>继承MyBatis-Plus的{@link BaseMapper}，提供对{@code clearing_account_balance}表的
 * 基础CRUD操作。</p>
 *
 * @author CFD Platform Team
 */
@Mapper
public interface AccountBalanceDbMapper extends BaseMapper<AccountBalanceEntity> {
}
