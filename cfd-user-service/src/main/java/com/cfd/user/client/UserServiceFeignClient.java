package com.cfd.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cfd.user.api.UserProfileResponse;
import com.cfd.user.api.UserRegistrationRequest;

@FeignClient(name = "cfd-user-service", path = "/users")
public interface UserServiceFeignClient {

    @PostMapping("/register")
    UserProfileResponse register(@RequestBody UserRegistrationRequest request);

    @PostMapping("/{userId}/kyc")
    UserProfileResponse submitKyc(@PathVariable("userId") String userId);

    @GetMapping("/{userId}")
    UserProfileResponse getById(@PathVariable("userId") String userId);
}
