package com.milktea.main.user.dto.response;

import com.milktea.main.user.entity.User;

public record ProfileInfoResponse(String username,
                                  String bio,
                                  String image,
                                  boolean following) {
    public ProfileInfoResponse(User user) {
        this(user.getUsername(), user.getBio(), user.getImage(), false);
    }

    public ProfileInfoResponse(User user, boolean following) {
        this(user.getUsername(), user.getBio(), user.getImage(), following);
    }
}
