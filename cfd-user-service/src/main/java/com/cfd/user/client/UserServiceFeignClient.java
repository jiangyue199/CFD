package com.cfd.user.client;

import com.cfd.user.api.UserProfileResponse;
import com.cfd.user.api.UserRegistrationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户服务 Feign 客户端接口。
 *
 * <p>供其他微服务通过 Feign 远程调用用户服务的注册、KYC 和查询接口。</p>
 *
 * @author CFD Platform Team
 */
@FeignClient(name = "cfd-user-service", path = "/users")
public interface UserServiceFeignClient {

    /**
     * 远程调用用户注册接口。
     *
     * @param request 注册请求
     * @return 注册后的用户资料
     */
    @PostMapping("/register")
    UserProfileResponse register(@RequestBody UserRegistrationRequest request);

    /**
     * 远程调用 KYC 提交接口。
     *
     * @param userId 用户ID
     * @return KYC 认证后的用户资料
     */
    @PostMapping("/{userId}/kyc")
    UserProfileResponse submitKyc(@PathVariable("userId") String userId);

    /**
     * 远程查询用户资料。
     *
     * @param userId 用户ID
     * @return 用户资料
     */
    @GetMapping("/{userId}")
    UserProfileResponse getById(@PathVariable("userId") String userId);
}
