package com.milktea.main.article.entity;

import com.milktea.util.TimestampEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "article_tb")
public class Article extends TimestampEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String slug;

    private String title;

    private String description;

    private String body;

    private long favoriteCount;
}
