package com.cfd.user.persistence;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface UserProfileDbMapper extends BaseMapper<UserProfileEntity> {
}
