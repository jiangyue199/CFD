package com.cfd.order.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单数据库 Mapper 接口。
 *
 * <p>基于 MyBatis-Plus 的 BaseMapper，提供对 order_record 表的基础 CRUD 操作。</p>
 *
 * @author CFD Platform Team
 */
@Mapper
public interface OrderDbMapper extends BaseMapper<OrderEntity> {
}
