package com.cfd.notification.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cfd.notification.api.NotificationRequest;
import com.cfd.notification.service.NotificationService;

@FeignClient(
        name = "cfd-notification-service",
        path = "/notifications",
        url = "${services.notification.url:http://localhost:8088}")
public interface NotificationServiceFeignClient {

    @PostMapping
    NotificationService.Notification send(@RequestBody NotificationRequest request);

    @GetMapping("/{userId}")
    List<NotificationService.Notification> listByUser(@PathVariable("userId") String userId);
}
