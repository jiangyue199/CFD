package com.cfd.notification.api;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cfd.notification.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public NotificationService.Notification send(@Validated @RequestBody NotificationRequest request) {
        return notificationService.notify(request.userId(), request.channel(), request.message());
    }

    @GetMapping("/{userId}")
    public List<NotificationService.Notification> listByUser(@PathVariable String userId) {
        return notificationService.listByUser(userId);
    }
}
