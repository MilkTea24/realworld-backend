package com.milktea.main.user.entity;

import com.milktea.main.util.TimestampEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Table(name = "user_tb")
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

    @OneToMany(mappedBy = "user")
    private final List<Authority> authorities = new ArrayList<>();

    @Builder
    public User(String email, String username, String bio, String image) {
        String inputBio = DEFAULT_BIO;
        if (!Objects.isNull(bio)) inputBio = bio;

        String inputImage = DEFAULT_IMAGE_PATH;
        if (!Objects.isNull(bio)) inputImage = image;

        this.email = email;
        this.username = username;
        this.bio = inputBio;
        this.image = inputImage;
    }

    public void setPassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void addAuthority(Authority authority) {
        authority.setUser(this);
        authorities.add(authority);
    }
}
