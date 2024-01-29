package com.milktea.main.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.milktea.main.user.entity.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record UserLoginResponse(@JsonProperty("user") UserLoginDTO userRegisterDTO) {
    public record UserLoginDTO(
            String email,
            String token,
            String username,
            String bio,
            String image) {
        public UserLoginDTO(User user){
            this(user.getEmail(), null, user.getUsername(), user.getBio(), user.getImage());
            log.debug("UserRegisterDTO 생성 - username = {}, email = {}", username, email);
        }
    }
}
