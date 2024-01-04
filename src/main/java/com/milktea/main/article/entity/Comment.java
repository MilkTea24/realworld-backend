package com.milktea.main.article.entity;

import com.milktea.main.user.entity.User;
import com.milktea.util.TimestampEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "comment_tb")
public class Comment extends TimestampEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;
}
