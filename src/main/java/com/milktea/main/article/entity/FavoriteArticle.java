package com.milktea.main.article.entity;

import com.milktea.main.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "favorite_article_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteArticle {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    public FavoriteArticle(User user, Article article) {
        this.user = user;
        this.article = article;
    }
}
