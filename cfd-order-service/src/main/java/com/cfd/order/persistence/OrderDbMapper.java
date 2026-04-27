package com.cfd.order.persistence;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface OrderDbMapper extends BaseMapper<OrderEntity> {
}
