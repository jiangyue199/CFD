package com.cfd.user.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegistrationRequest(
        @NotBlank String username,
        @Email @NotBlank String email,
        @NotBlank String phone
) {
}
