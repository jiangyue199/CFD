package com.cfd.risk.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 持仓限额数据库Mapper。
 */
@Mapper
public interface PositionLimitDbMapper extends BaseMapper<PositionLimitEntity> {
}
