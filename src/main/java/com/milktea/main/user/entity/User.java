package com.milktea.main.user.entity;

import com.milktea.util.TimestampEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_id")
public class User extends TimestampEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String email;

    private String username;

    private String bio;

    private String image;
}
