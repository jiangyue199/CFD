package com.cfd.risk.persistence;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 风控规则数据库 Mapper 接口。
 *
 * <p>基于 MyBatis-Plus 的 BaseMapper，提供对 risk_rule 表的基础 CRUD 操作。</p>
 *
 * @author CFD Platform Team
 */
@Mapper
public interface RiskRuleDbMapper extends BaseMapper<RiskRuleEntity> {
}
