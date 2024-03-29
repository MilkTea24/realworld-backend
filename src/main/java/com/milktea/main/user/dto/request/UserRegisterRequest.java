package com.milktea.main.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.milktea.main.user.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record UserRegisterRequest(@JsonProperty("user") @Valid UserRegisterDTO userRegisterDTO) {
    public record UserRegisterDTO(
            @NotNull String username,
            @NotNull @Email String email,
            String bio,
            String image,
            @NotNull String password) {
        public UserRegisterDTO{
            log.debug("UserRegisterDTO 생성 - username = {}, email = {}", username, email);
        }

        //테스트 데이터 생성용
        public UserRegisterDTO(User user) {
            this(user.getUsername(), user.getEmail(), user.getBio(), user.getImage(), user.getPassword());
        }
    }
}