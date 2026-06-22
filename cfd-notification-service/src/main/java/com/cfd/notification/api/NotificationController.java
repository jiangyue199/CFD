package com.cfd.notification.api;

import com.cfd.notification.service.NotificationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知 REST 控制器。
 *
 * <p>提供通知发送和用户通知列表查询的 HTTP 接口，
 * 所有请求路径以 {@code /notifications} 为前缀。</p>
 *
 * @author CFD Platform Team
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 构造通知控制器。
     *
     * @param notificationService 通知服务
     */
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 发送通知。
     *
     * @param request 包含用户ID、通知渠道和消息内容的请求
     * @return 已发送的通知信息
     */
    @PostMapping
    public NotificationService.Notification send(@Validated @RequestBody NotificationRequest request) {
        return notificationService.notify(request.userId(), request.channel(), request.message());
    }

    /**
     * 查询指定用户的通知列表。
     *
     * @param userId 用户ID
     * @return 该用户的通知列表，按发送时间降序排列
     */
    @GetMapping("/{userId}")
    public List<NotificationService.Notification> listByUser(@PathVariable String userId) {
        return notificationService.listByUser(userId);
    }
}
