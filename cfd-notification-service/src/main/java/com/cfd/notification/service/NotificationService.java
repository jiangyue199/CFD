package com.cfd.notification.service;

import java.time.Instant;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import com.cfd.notification.persistence.NotificationDbMapper;
import com.cfd.notification.persistence.NotificationEntity;

@Service
public class NotificationService {

    private final NotificationDbMapper notificationDbMapper;

    public NotificationService(NotificationDbMapper notificationDbMapper) {
        this.notificationDbMapper = notificationDbMapper;
    }

    public synchronized Notification notify(String userId, String channel, String message) {
        Notification notification = new Notification(userId, channel, message, Instant.now());
        NotificationEntity entity = new NotificationEntity();
        entity.setUserId(userId);
        entity.setChannel(channel);
        entity.setMessage(message);
        entity.setSentAt(notification.sentAt());
        notificationDbMapper.insert(entity);
        return notification;
    }

    public synchronized List<Notification> listByUser(String userId) {
        return notificationDbMapper.selectList(new LambdaQueryWrapper<NotificationEntity>()
                        .eq(NotificationEntity::getUserId, userId)
                        .orderByDesc(NotificationEntity::getSentAt))
                .stream()
                .map(entity -> new Notification(
                        entity.getUserId(),
                        entity.getChannel(),
                        entity.getMessage(),
                        entity.getSentAt()))
                .toList();
    }

    public record Notification(String userId, String channel, String message, Instant sentAt) {}
}
