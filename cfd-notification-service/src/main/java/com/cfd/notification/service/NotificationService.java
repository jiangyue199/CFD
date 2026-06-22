package com.cfd.notification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.notification.persistence.NotificationDbMapper;
import com.cfd.notification.persistence.NotificationEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * 通知服务。
 *
 * <p>封装通知发送和查询的业务逻辑，支持多渠道通知消息的持久化和检索。</p>
 *
 * @author CFD Platform Team
 */
@Service
public class NotificationService {

    private final NotificationDbMapper notificationDbMapper;

    /**
     * 构造通知服务。
     *
     * @param notificationDbMapper 通知消息数据库 Mapper
     */
    public NotificationService(NotificationDbMapper notificationDbMapper) {
        this.notificationDbMapper = notificationDbMapper;
    }

    /**
     * 发送通知消息。
     *
     * <p>创建通知记录并持久化到数据库。</p>
     *
     * @param userId  目标用户ID
     * @param channel 通知渠道
     * @param message 消息内容
     * @return 已发送的通知信息
     */
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

    /**
     * 查询指定用户的通知列表。
     *
     * @param userId 用户ID
     * @return 该用户的通知列表，按发送时间降序排列
     */
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

    /**
     * 通知消息记录。
     *
     * @param userId  目标用户ID
     * @param channel 通知渠道
     * @param message 消息内容
     * @param sentAt  发送时间
     */
    public record Notification(String userId, String channel, String message, Instant sentAt) {}
}
