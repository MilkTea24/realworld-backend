package com.milktea.main.user.entity;

import com.milktea.main.util.TimestampEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends TimestampEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String email;

    private String username;

    private String bio;

    private String image;

    @Builder
    public User(String email, String username, String bio, String image) {
        this.email = email;
        this.username = username;
        this.bio = bio;
        this.image = image;
    }
}
