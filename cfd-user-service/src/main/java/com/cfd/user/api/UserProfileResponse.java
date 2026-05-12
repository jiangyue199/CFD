package com.cfd.user.api;

import java.time.Instant;

/**
 * 用户资料响应 DTO。
 *
 * <p>用于向客户端返回用户资料信息。</p>
 *
 * @param userId    用户ID
 * @param username  用户名
 * @param email     邮箱地址
 * @param phone     手机号码
 * @param kycPassed 是否通过 KYC 认证
 * @param createdAt 创建时间
 * @author CFD Platform Team
 */
public record UserProfileResponse(
        String userId,
        String username,
        String email,
        String phone,
        boolean kycPassed,
        Instant createdAt
) {
}
