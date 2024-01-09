package com.milktea.main.user.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "authority_tb")
public class Authority {
    @Id
    @GeneratedValue
    private Integer id;

    @Getter
    private String name;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
