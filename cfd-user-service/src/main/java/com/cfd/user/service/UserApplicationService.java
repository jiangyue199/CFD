package com.cfd.user.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cfd.user.domain.UserProfile;
import com.cfd.user.domain.UserRepository;

/**
 * 用户应用服务。
 *
 * <p>封装用户相关的业务逻辑，包括注册、KYC 认证提交和用户查询，
 * 作为控制器与领域层之间的中介。</p>
 *
 * @author CFD Platform Team
 */
@Service
public class UserApplicationService {

    private final UserRepository userRepository;

    /**
     * 构造用户应用服务。
     *
     * @param userRepository 用户仓储
     */
    public UserApplicationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 注册新用户。
     *
     * <p>若用户名已存在则抛出异常，否则生成唯一ID并保存用户资料。</p>
     *
     * @param username 用户名
     * @param email    邮箱地址
     * @param phone    手机号码
     * @return 注册成功的用户资料
     * @throws IllegalArgumentException 当用户名已存在时抛出
     */
    public UserProfile register(String username, String email, String phone) {
        userRepository.findByUsername(username).ifPresent(existing -> {
            throw new IllegalArgumentException("Username already exists");
        });
        UserProfile profile = new UserProfile(UUID.randomUUID().toString(), username, email, phone);
        return userRepository.save(profile);
    }

    /**
     * 提交 KYC 认证。
     *
     * @param userId 用户ID
     * @return KYC 认证后的用户资料
     * @throws IllegalArgumentException 当用户不存在时抛出
     */
    public UserProfile submitKyc(String userId) {
        UserProfile profile = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        profile.markKycPassed();
        userRepository.save(profile);
        return profile;
    }

    /**
     * 根据用户ID查询用户资料。
     *
     * @param userId 用户ID
     * @return 用户资料
     * @throws IllegalArgumentException 当用户不存在时抛出
     */
    public UserProfile getById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
