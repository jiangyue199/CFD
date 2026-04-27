package com.cfd.user.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.user.domain.UserProfile;
import com.cfd.user.domain.UserRepository;

@Repository
public class MybatisPlusUserRepository implements UserRepository {

    private final UserProfileDbMapper mapper;

    public MybatisPlusUserRepository(UserProfileDbMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public UserProfile save(UserProfile profile) {
        UserProfileEntity entity = toEntity(profile);
        if (mapper.selectById(profile.getUserId()) == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return profile;
    }

    @Override
    public Optional<UserProfile> findById(String userId) {
        return Optional.ofNullable(mapper.selectById(userId)).map(this::toDomain);
    }

    @Override
    public Optional<UserProfile> findByUsername(String username) {
        UserProfileEntity entity = mapper.selectOne(new LambdaQueryWrapper<UserProfileEntity>()
                .eq(UserProfileEntity::getUsername, username)
                .last("LIMIT 1"));
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    private UserProfileEntity toEntity(UserProfile profile) {
        UserProfileEntity entity = new UserProfileEntity();
        entity.setUserId(profile.getUserId());
        entity.setUsername(profile.getUsername());
        entity.setEmail(profile.getEmail());
        entity.setPhone(profile.getPhone());
        entity.setKycPassed(profile.isKycPassed());
        entity.setCreatedAt(profile.getCreatedAt());
        return entity;
    }

    private UserProfile toDomain(UserProfileEntity entity) {
        return UserProfile.restore(
                entity.getUserId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getKycPassed(),
                entity.getCreatedAt());
    }
}
