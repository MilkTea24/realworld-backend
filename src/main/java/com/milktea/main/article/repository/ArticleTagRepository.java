package com.milktea.main.article.repository;


import com.milktea.main.article.entity.ArticleTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {
}
