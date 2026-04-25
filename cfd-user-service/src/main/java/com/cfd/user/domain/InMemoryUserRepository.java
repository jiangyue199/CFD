package com.cfd.user.domain;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {

    private final Map<String, UserProfile> byId = new ConcurrentHashMap<>();
    private final Map<String, String> usernameToId = new ConcurrentHashMap<>();

    @Override
    public synchronized UserProfile save(UserProfile profile) {
        byId.put(profile.getUserId(), profile);
        usernameToId.put(profile.getUsername(), profile.getUserId());
        return profile;
    }

    @Override
    public Optional<UserProfile> findById(String userId) {
        return Optional.ofNullable(byId.get(userId));
    }

    @Override
    public Optional<UserProfile> findByUsername(String username) {
        String userId = usernameToId.get(username);
        if (userId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(byId.get(userId));
    }
}
