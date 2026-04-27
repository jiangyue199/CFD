package com.cfd.user.domain;

import java.time.Instant;

public class UserProfile {

    private final String userId;
    private final String username;
    private final String email;
    private final String phone;
    private boolean kycPassed;
    private final Instant createdAt;

    public UserProfile(String userId, String username, String email, String phone) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.kycPassed = false;
        this.createdAt = Instant.now();
    }

    public static UserProfile restore(String userId,
                                      String username,
                                      String email,
                                      String phone,
                                      boolean kycPassed,
                                      Instant createdAt) {
        return new UserProfile(userId, username, email, phone, kycPassed, createdAt);
    }

    private UserProfile(String userId,
                        String username,
                        String email,
                        String phone,
                        boolean kycPassed,
                        Instant createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.kycPassed = kycPassed;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isKycPassed() {
        return kycPassed;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void markKycPassed() {
        this.kycPassed = true;
    }
}
