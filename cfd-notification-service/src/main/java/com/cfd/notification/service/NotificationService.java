package com.cfd.notification.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final List<Notification> notifications = new ArrayList<>();

    public synchronized Notification notify(String userId, String channel, String message) {
        Notification notification = new Notification(userId, channel, message, Instant.now());
        notifications.add(notification);
        return notification;
    }

    public synchronized List<Notification> listByUser(String userId) {
        return notifications.stream().filter(notification -> notification.userId().equals(userId)).toList();
    }

    public record Notification(String userId, String channel, String message, Instant sentAt) {}
}
