package com.milktea.main.article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tag_tb")
public class Tag {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}
