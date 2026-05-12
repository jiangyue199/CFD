package com.cfd.user.domain;

import java.time.Instant;

/**
 * 用户资料领域模型。
 *
 * <p>表示平台用户的基本信息，包括用户名、邮箱、手机号、KYC 认证状态和创建时间。</p>
 *
 * @author CFD Platform Team
 */
public class UserProfile {

    /** 用户ID */
    private final String userId;
    /** 用户名 */
    private final String username;
    /** 邮箱地址 */
    private final String email;
    /** 手机号码 */
    private final String phone;
    /** 是否通过 KYC 认证 */
    private boolean kycPassed;
    /** 创建时间 */
    private final Instant createdAt;

    /**
     * 创建新用户资料（KYC 默认未通过，创建时间为当前时刻）。
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param email    邮箱地址
     * @param phone    手机号码
     */
    public UserProfile(String userId, String username, String email, String phone) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.kycPassed = false;
        this.createdAt = Instant.now();
    }

    /**
     * 从持久化数据恢复用户资料（工厂方法）。
     *
     * @param userId    用户ID
     * @param username  用户名
     * @param email     邮箱地址
     * @param phone     手机号码
     * @param kycPassed KYC 认证状态
     * @param createdAt 创建时间
     * @return 恢复的用户资料对象
     */
    public static UserProfile restore(String userId,
                                      String username,
                                      String email,
                                      String phone,
                                      boolean kycPassed,
                                      Instant createdAt) {
        return new UserProfile(userId, username, email, phone, kycPassed, createdAt);
    }

    /**
     * 私有全参构造器，供 {@link #restore} 方法使用。
     */
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

    /** @return 用户ID */
    public String getUserId() {
        return userId;
    }

    /** @return 用户名 */
    public String getUsername() {
        return username;
    }

    /** @return 邮箱地址 */
    public String getEmail() {
        return email;
    }

    /** @return 手机号码 */
    public String getPhone() {
        return phone;
    }

    /** @return 是否通过 KYC 认证 */
    public boolean isKycPassed() {
        return kycPassed;
    }

    /** @return 创建时间 */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * 标记 KYC 认证通过。
     */
    public void markKycPassed() {
        this.kycPassed = true;
    }
}
