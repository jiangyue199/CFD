package com.cfd.risk.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日交易限额数据库Mapper。
 */
@Mapper
public interface DailyTradeLimitDbMapper extends BaseMapper<DailyTradeLimitEntity> {
}
