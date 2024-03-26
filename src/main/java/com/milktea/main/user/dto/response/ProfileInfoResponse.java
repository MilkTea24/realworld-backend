package com.milktea.main.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.milktea.main.user.entity.User;

public record ProfileInfoResponse(@JsonProperty("profile") ProfileInfoDTO profileInfoDTO) {
    public record ProfileInfoDTO(String username,
                                 String bio,
                                 String image,
                                 boolean following) {
        public ProfileInfoDTO(User user) {
            this(user.getUsername(), user.getBio(), user.getImage(), false);
        }
        public ProfileInfoDTO(User user, boolean following) {
            this(user.getUsername(), user.getBio(), user.getImage(), following);
        }
    }
}
