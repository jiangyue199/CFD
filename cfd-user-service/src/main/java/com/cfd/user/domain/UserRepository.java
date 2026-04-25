package com.cfd.user.domain;

import java.util.Optional;

public interface UserRepository {

    UserProfile save(UserProfile profile);

    Optional<UserProfile> findById(String userId);

    Optional<UserProfile> findByUsername(String username);
}
