package com.cfd.configservice.persistence;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface RuntimeConfigDbMapper extends BaseMapper<RuntimeConfigEntity> {
}
