package com.cfd.notification.api;

import jakarta.validation.constraints.NotBlank;

/**
 * 通知请求 DTO。
 *
 * <p>用于接收发送通知的请求参数。</p>
 *
 * @param userId  目标用户ID，不能为空
 * @param channel 通知渠道（如 SMS、EMAIL 等），不能为空
 * @param message 通知消息内容，不能为空
 * @author CFD Platform Team
 */
public record NotificationRequest(
        @NotBlank String userId,
        @NotBlank String channel,
        @NotBlank String message
) {
}
