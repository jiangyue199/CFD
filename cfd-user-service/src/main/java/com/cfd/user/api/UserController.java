package com.cfd.user.api;

import com.cfd.user.domain.UserProfile;
import com.cfd.user.service.UserApplicationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户 REST 控制器。
 *
 * <p>提供用户注册、KYC 提交和用户信息查询的 HTTP 接口，
 * 所有请求路径以 {@code /users} 为前缀。</p>
 *
 * @author CFD Platform Team
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserApplicationService userApplicationService;

    /**
     * 构造用户控制器。
     *
     * @param userApplicationService 用户应用服务
     */
    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    /**
     * 用户注册。
     *
     * @param request 包含用户名、邮箱和手机号的注册请求
     * @return 注册成功后的用户资料响应
     */
    @PostMapping("/register")
    public UserProfileResponse register(@Validated @RequestBody UserRegistrationRequest request) {
        return toResponse(userApplicationService.register(request.username(), request.email(), request.phone()));
    }

    /**
     * 提交 KYC 认证。
     *
     * @param userId 用户ID
     * @return KYC 认证后的用户资料响应
     */
    @PostMapping("/{userId}/kyc")
    public UserProfileResponse submitKyc(@PathVariable String userId) {
        return toResponse(userApplicationService.submitKyc(userId));
    }

    /**
     * 根据用户ID查询用户资料。
     *
     * @param userId 用户ID
     * @return 用户资料响应
     */
    @GetMapping("/{userId}")
    public UserProfileResponse getById(@PathVariable String userId) {
        return toResponse(userApplicationService.getById(userId));
    }

    /**
     * 将领域模型转换为 API 响应对象。
     *
     * @param profile 用户资料领域对象
     * @return 用户资料响应
     */
    private UserProfileResponse toResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getUserId(),
                profile.getUsername(),
                profile.getEmail(),
                profile.getPhone(),
                profile.isKycPassed(),
                profile.getCreatedAt()
        );
    }
}
