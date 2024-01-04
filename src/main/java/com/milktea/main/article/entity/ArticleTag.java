package com.milktea.main.article.entity;

import com.milktea.util.TimestampEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "article_tag_tb")
public class ArticleTag {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
}
