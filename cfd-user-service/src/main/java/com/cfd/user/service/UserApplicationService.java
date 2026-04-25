package com.cfd.user.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cfd.user.domain.UserProfile;
import com.cfd.user.domain.UserRepository;

@Service
public class UserApplicationService {

    private final UserRepository userRepository;

    public UserApplicationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfile register(String username, String email, String phone) {
        userRepository.findByUsername(username).ifPresent(existing -> {
            throw new IllegalArgumentException("Username already exists");
        });
        UserProfile profile = new UserProfile(UUID.randomUUID().toString(), username, email, phone);
        return userRepository.save(profile);
    }

    public UserProfile submitKyc(String userId) {
        UserProfile profile = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        profile.markKycPassed();
        userRepository.save(profile);
        return profile;
    }

    public UserProfile getById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
