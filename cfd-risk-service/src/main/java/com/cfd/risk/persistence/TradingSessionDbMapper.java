package com.cfd.risk.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 交易时段数据库Mapper。
 */
@Mapper
public interface TradingSessionDbMapper extends BaseMapper<TradingSessionEntity> {
}
