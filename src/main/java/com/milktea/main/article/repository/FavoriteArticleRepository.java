package com.milktea.main.article.repository;

import com.milktea.main.article.entity.FavoriteArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteArticleRepository extends JpaRepository<FavoriteArticle, Long> {
}
