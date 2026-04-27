package com.cfd.backend.persistence;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface DashboardMetricDbMapper extends BaseMapper<DashboardMetricEntity> {
}
