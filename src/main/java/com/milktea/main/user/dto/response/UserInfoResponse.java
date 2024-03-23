package com.milktea.main.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.milktea.main.user.entity.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record UserInfoResponse(@JsonProperty("user") UserInfoDTO userInfoDTO) {
    public record UserInfoDTO(
            String email,
            String token,
            String username,
            String bio,
            String image) {
        public UserInfoDTO(User user, String token){
            this(user.getEmail(), token, user.getUsername(), user.getBio(), user.getImage());
            log.debug("UserInfoDTO 생성 - username = {}, email = {}", username, email);
        }
    }
}