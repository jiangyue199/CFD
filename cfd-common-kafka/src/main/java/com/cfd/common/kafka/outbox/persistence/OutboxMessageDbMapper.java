package com.cfd.common.kafka.outbox.persistence;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface OutboxMessageDbMapper extends BaseMapper<OutboxMessageEntity> {
}
