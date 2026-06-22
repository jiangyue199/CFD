package com.cfd.trading.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 持仓数据库Mapper接口。
 *
 * <p>继承MyBatis-Plus的{@link BaseMapper}，提供对{@code t_open_position}表的
 * 基础CRUD操作。</p>
 *
 * @author CFD Platform Team
 */
@Mapper
public interface OpenPositionDbMapper extends BaseMapper<OpenPositionEntity> {
}
