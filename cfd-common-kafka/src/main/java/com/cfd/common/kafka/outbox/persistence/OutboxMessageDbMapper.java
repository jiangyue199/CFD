package com.cfd.common.kafka.outbox.persistence;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 发件箱消息 MyBatis-Plus Mapper 接口。
 *
 * <p>继承 {@link BaseMapper}，自动获得对 {@code cfd_outbox_message} 表的基本 CRUD 操作。
 * 由 {@link MybatisPlusOutboxRepository} 使用，完成发件箱消息的持久化读写。
 *
 * @author CFD Platform Team
 * @see OutboxMessageEntity
 * @see MybatisPlusOutboxRepository
 */
@Mapper
public interface OutboxMessageDbMapper extends BaseMapper<OutboxMessageEntity> {
}
