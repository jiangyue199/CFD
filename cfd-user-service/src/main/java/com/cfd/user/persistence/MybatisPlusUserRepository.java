package com.cfd.user.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.user.domain.UserProfile;
import com.cfd.user.domain.UserRepository;

/**
 * 基于 MyBatis-Plus 的用户仓储实现。
 *
 * <p>通过 {@link UserProfileDbMapper} 与数据库交互，
 * 实现用户资料的持久化存储和查询。</p>
 *
 * @author CFD Platform Team
 */
@Repository
public class MybatisPlusUserRepository implements UserRepository {

    private final UserProfileDbMapper mapper;

    /**
     * 构造 MyBatis-Plus 用户仓储。
     *
     * @param mapper 用户资料数据库 Mapper
     */
    public MybatisPlusUserRepository(UserProfileDbMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     *
     * <p>若用户不存在则插入，否则更新。</p>
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserProfile> findById(String userId) {
        return Optional.ofNullable(mapper.selectById(userId)).map(this::toDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserProfile> findByUsername(String username) {
        UserProfileEntity entity = mapper.selectOne(new LambdaQueryWrapper<UserProfileEntity>()
                .eq(UserProfileEntity::getUsername, username)
                .last("LIMIT 1"));
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    /**
     * 将领域模型转换为数据库实体。
     *
     * @param profile 用户资料领域对象
     * @return 数据库实体
     */
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

    /**
     * 将数据库实体转换为领域模型。
     *
     * @param entity 数据库实体
     * @return 用户资料领域对象
     */
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
