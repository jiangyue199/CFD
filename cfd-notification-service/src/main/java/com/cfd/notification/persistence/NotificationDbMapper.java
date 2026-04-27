package com.cfd.notification.persistence;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface NotificationDbMapper extends BaseMapper<NotificationEntity> {
}
