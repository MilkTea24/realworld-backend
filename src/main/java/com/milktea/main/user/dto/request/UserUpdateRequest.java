package com.milktea.main.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.milktea.main.user.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

//수정 가능한 필드만 작성해야 한다.
@Slf4j
public record UserUpdateRequest(@JsonProperty("user") @Valid @NotNull UserUpdateDTO userUpdateDTO) {
    public record UserUpdateDTO(
            String username,
            @Email String email,
            String bio,
            String image,
            String password) {
        public UserUpdateDTO{
            log.debug("UserUpdateDTO 생성 - username = {}, email = {}", username, email);
        }

        //테스트 데이터 생성용
        public UserUpdateDTO(User user) {
            this(user.getUsername(), user.getEmail(), user.getBio(), user.getImage(), user.getPassword());
        }
    }
}