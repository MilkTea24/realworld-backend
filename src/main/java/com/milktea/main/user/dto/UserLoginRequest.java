package com.milktea.main.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.milktea.main.user.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record UserLoginRequest(@JsonProperty("user") @SerializedName("user") @Valid UserLoginDTO userLoginDTO) {
    public record UserLoginDTO(
            @NotNull @Email String email,
            @NotNull String password) {
        public UserLoginDTO{
            log.debug("UserLoginDTO 생성 - email = {}", email);
        }

        //테스트 데이터 생성용
        public UserLoginDTO(User user) {
            this(user.getEmail(), user.getPassword());
        }
    }
}

