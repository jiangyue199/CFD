package com.cfd.marketdata.persistence;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface MarketQuoteDbMapper extends BaseMapper<MarketQuoteEntity> {
}
