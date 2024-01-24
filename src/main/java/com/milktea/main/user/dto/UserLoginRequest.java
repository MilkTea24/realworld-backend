package com.milktea.main.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserLoginRequest(
        String username,
        @NotNull @Email String email,
        String bio,
        String image,
        @NotNull String password) {}

