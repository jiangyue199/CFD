package com.cfd.risk.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 黑白名单数据库Mapper。
 */
@Mapper
public interface RiskBlacklistDbMapper extends BaseMapper<RiskBlacklistEntity> {
}
