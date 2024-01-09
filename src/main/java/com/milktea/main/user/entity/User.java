package com.milktea.main.user.entity;

import com.milktea.main.util.TimestampEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "user_id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends TimestampEntity {
    private static final String DEFAULT_BIO = "Default Bio";
    private static final String DEFAULT_IMAGE_PATH = null;

    @Id
    @GeneratedValue
    private Long id;

    private String email;

    private String username;

    private String bio;

    private String image;

    private String password;

    @Builder
    public User(String email, String username, String bio, String image, String password) {
        String inputBio = DEFAULT_BIO;
        if (!Objects.isNull(bio)) inputBio = bio;

        String inputImage = DEFAULT_IMAGE_PATH;
        if (!Objects.isNull(bio)) inputImage = image;

        this.email = email;
        this.username = username;
        this.bio = inputBio;
        this.image = inputImage;
        this.password = password;
    }
}