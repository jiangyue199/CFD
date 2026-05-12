package com.cfd.notification.persistence;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 通知消息数据库实体。
 *
 * <p>映射数据库表 {@code notification_message}，存储用户通知的渠道、内容和发送时间。</p>
 *
 * @author CFD Platform Team
 */
@TableName("notification_message")
public class NotificationEntity {

    /** 自增主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 目标用户ID */
    @TableField("user_id")
    private String userId;

    /** 通知渠道 */
    @TableField("channel")
    private String channel;

    /** 通知消息内容 */
    @TableField("message")
    private String message;

    /** 发送时间 */
    @TableField("sent_at")
    private Instant sentAt;

    /** @return 自增主键 */
    public Long getId() {
        return id;
    }

    /** @param id 自增主键 */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return 目标用户ID */
    public String getUserId() {
        return userId;
    }

    /** @param userId 目标用户ID */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** @return 通知渠道 */
    public String getChannel() {
        return channel;
    }

    /** @param channel 通知渠道 */
    public void setChannel(String channel) {
        this.channel = channel;
    }

    /** @return 通知消息内容 */
    public String getMessage() {
        return message;
    }

    /** @param message 通知消息内容 */
    public void setMessage(String message) {
        this.message = message;
    }

    /** @return 发送时间 */
    public Instant getSentAt() {
        return sentAt;
    }

    /** @param sentAt 发送时间 */
    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }
}
