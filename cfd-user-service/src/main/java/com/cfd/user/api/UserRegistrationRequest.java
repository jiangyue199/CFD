package com.cfd.user.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 用户注册请求 DTO。
 *
 * <p>用于接收用户注册时提交的基本信息。</p>
 *
 * @param username 用户名，不能为空
 * @param email    邮箱地址，必须符合邮箱格式且不能为空
 * @param phone    手机号码，不能为空
 * @author CFD Platform Team
 */
public record UserRegistrationRequest(
        @NotBlank String username,
        @Email @NotBlank String email,
        @NotBlank String phone
) {
}
