package com.cfd.user.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cfd.user.domain.UserProfile;
import com.cfd.user.service.UserApplicationService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserApplicationService userApplicationService;

    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @PostMapping("/register")
    public UserProfileResponse register(@Validated @RequestBody UserRegistrationRequest request) {
        return toResponse(userApplicationService.register(request.username(), request.email(), request.phone()));
    }

    @PostMapping("/{userId}/kyc")
    public UserProfileResponse submitKyc(@PathVariable String userId) {
        return toResponse(userApplicationService.submitKyc(userId));
    }

    @GetMapping("/{userId}")
    public UserProfileResponse getById(@PathVariable String userId) {
        return toResponse(userApplicationService.getById(userId));
    }

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
