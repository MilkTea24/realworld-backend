package com.milktea.main.user.dto;

public record UserLoginResponse (String email,
                                 String token,
                                 String username,
                                 String bio,
                                 String image) {
}
