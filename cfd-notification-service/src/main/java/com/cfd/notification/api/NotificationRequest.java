package com.cfd.notification.api;

import jakarta.validation.constraints.NotBlank;

public record NotificationRequest(
        @NotBlank String userId,
        @NotBlank String channel,
        @NotBlank String message
) {
}
