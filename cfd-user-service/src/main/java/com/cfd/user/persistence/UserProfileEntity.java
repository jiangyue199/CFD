package com.cfd.user.persistence;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 用户资料数据库实体。
 *
 * <p>映射数据库表 {@code user_profile}，存储用户的基本信息和 KYC 认证状态。</p>
 *
 * @author CFD Platform Team
 */
@TableName("user_profile")
public class UserProfileEntity {

    /** 用户ID（主键） */
    @TableId(value = "user_id", type = IdType.INPUT)
    private String userId;

    /** 用户名 */
    @TableField("username")
    private String username;

    /** 邮箱地址 */
    @TableField("email")
    private String email;

    /** 手机号码 */
    @TableField("phone")
    private String phone;

    /** KYC 认证状态 */
    @TableField("kyc_passed")
    private Boolean kycPassed;

    /** 创建时间 */
    @TableField("created_at")
    private Instant createdAt;

    /** @return 用户ID */
    public String getUserId() {
        return userId;
    }

    /** @param userId 用户ID */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** @return 用户名 */
    public String getUsername() {
        return username;
    }

    /** @param username 用户名 */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return 邮箱地址 */
    public String getEmail() {
        return email;
    }

    /** @param email 邮箱地址 */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return 手机号码 */
    public String getPhone() {
        return phone;
    }

    /** @param phone 手机号码 */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** @return KYC 认证状态 */
    public Boolean getKycPassed() {
        return kycPassed;
    }

    /** @param kycPassed KYC 认证状态 */
    public void setKycPassed(Boolean kycPassed) {
        this.kycPassed = kycPassed;
    }

    /** @return 创建时间 */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /** @param createdAt 创建时间 */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
