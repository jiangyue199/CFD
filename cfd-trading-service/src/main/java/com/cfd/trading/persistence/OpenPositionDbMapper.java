package com.cfd.trading.persistence;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface OpenPositionDbMapper extends BaseMapper<OpenPositionEntity> {
}
