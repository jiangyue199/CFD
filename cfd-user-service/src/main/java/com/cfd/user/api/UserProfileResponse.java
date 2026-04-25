package com.cfd.user.api;

import java.time.Instant;

public record UserProfileResponse(
        String userId,
        String username,
        String email,
        String phone,
        boolean kycPassed,
        Instant createdAt
) {
}
