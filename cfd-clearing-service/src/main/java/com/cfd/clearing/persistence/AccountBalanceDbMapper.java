package com.cfd.clearing.persistence;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface AccountBalanceDbMapper extends BaseMapper<AccountBalanceEntity> {
}
