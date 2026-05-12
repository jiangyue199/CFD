package com.cfd.user.domain;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的用户仓储实现。
 *
 * <p>使用 {@link ConcurrentHashMap} 在内存中存储用户资料数据，
 * 适用于测试或无需持久化的场景。维护按ID和按用户名两个索引。</p>
 *
 * @author CFD Platform Team
 */
public class InMemoryUserRepository implements UserRepository {

    /** 按用户ID索引 */
    private final Map<String, UserProfile> byId = new ConcurrentHashMap<>();
    /** 用户名到用户ID的映射 */
    private final Map<String, String> usernameToId = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized UserProfile save(UserProfile profile) {
        byId.put(profile.getUserId(), profile);
        usernameToId.put(profile.getUsername(), profile.getUserId());
        return profile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserProfile> findById(String userId) {
        return Optional.ofNullable(byId.get(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserProfile> findByUsername(String username) {
        String userId = usernameToId.get(username);
        if (userId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(byId.get(userId));
    }
}
