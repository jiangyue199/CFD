package com.cfd.configservice.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 运行时配置数据库 Mapper 接口。
 *
 * <p>继承 MyBatis-Plus {@link BaseMapper}，提供对 {@link RuntimeConfigEntity} 的基本 CRUD 操作。</p>
 *
 * @author CFD Platform Team
 */
@Mapper
public interface RuntimeConfigDbMapper extends BaseMapper<RuntimeConfigEntity> {
}
