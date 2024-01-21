package com.milktea.main.user.dto;

import com.milktea.main.user.entity.User;

public record UserRegisterResponse (String email,
                                    String token,
                                    String username,
                                    String bio,
                                    String image) {
    public UserRegisterResponse(User user) {
        this(user.getEmail(), null, user.getUsername(), user.getBio(), user.getImage());
    }
}