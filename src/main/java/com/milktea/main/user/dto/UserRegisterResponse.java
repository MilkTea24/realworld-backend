package com.milktea.main.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.milktea.main.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record UserRegisterResponse(@JsonProperty("user") UserRegisterDTO userRegisterDTO) {
    public record UserRegisterDTO(
            String email,
            String token,
            String username,
            String bio,
            String image) {
        public UserRegisterDTO(User user){
            this(user.getEmail(), null, user.getUsername(), user.getBio(), user.getImage());
            log.debug("UserRegisterDTO 생성 - username = {}, email = {}", username, email);
        }
    }
}