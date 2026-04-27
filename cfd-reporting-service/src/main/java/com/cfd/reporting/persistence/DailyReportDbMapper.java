package com.cfd.reporting.persistence;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface DailyReportDbMapper extends BaseMapper<DailyReportEntity> {
}
