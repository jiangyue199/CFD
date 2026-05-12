package com.cfd.notification.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cfd.notification.api.NotificationRequest;
import com.cfd.notification.service.NotificationService;

/**
 * 通知服务 Feign 客户端接口。
 *
 * <p>供其他微服务通过 Feign 远程调用通知服务的发送和查询接口。</p>
 *
 * @author CFD Platform Team
 */
@FeignClient(
        name = "cfd-notification-service",
        path = "/notifications",
        url = "${services.notification.url:http://localhost:8088}")
public interface NotificationServiceFeignClient {

    /**
     * 远程调用通知发送接口。
     *
     * @param request 通知发送请求
     * @return 已发送的通知信息
     */
    @PostMapping
    NotificationService.Notification send(@RequestBody NotificationRequest request);

    /**
     * 远程查询指定用户的通知列表。
     *
     * @param userId 用户ID
     * @return 该用户的通知列表
     */
    @GetMapping("/{userId}")
    List<NotificationService.Notification> listByUser(@PathVariable("userId") String userId);
}
