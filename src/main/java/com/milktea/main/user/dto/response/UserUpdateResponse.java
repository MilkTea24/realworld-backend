package com.milktea.main.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.milktea.main.user.entity.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record UserUpdateResponse(@JsonProperty("user") UserUpdateDTO userUpdateDTO) {
    public record UserUpdateDTO(
            String email,
            String token,
            String username,
            String bio,
            String image) {
        public UserUpdateDTO(User user){
            this(user.getEmail(), null, user.getUsername(), user.getBio(), user.getImage());
            log.debug("UserUpdateDTO 생성 - username = {}, email = {}", username, email);
        }
    }
}