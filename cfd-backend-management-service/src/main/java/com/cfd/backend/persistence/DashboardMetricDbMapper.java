package com.cfd.backend.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仪表盘指标数据库 Mapper 接口。
 *
 * <p>继承 MyBatis-Plus {@link BaseMapper}，提供对 {@link DashboardMetricEntity} 的基本 CRUD 操作。</p>
 *
 * @author CFD Platform Team
 */
@Mapper
public interface DashboardMetricDbMapper extends BaseMapper<DashboardMetricEntity> {
}
