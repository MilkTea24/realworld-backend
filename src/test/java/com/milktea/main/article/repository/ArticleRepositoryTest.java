package com.milktea.main.article.repository;

import com.milktea.main.article.entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;


@DataJpaTest
@Slf4j
public class ArticleRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;

    private static Article article;

    @BeforeEach
    void setup() {
        log.info("------------------setup------------------");
        article = Article.builder()
                .title("test title1")
                .slug("test-title1")
                .description("test description1")
                .body("test body1")
                .build();

        log.info(article.toString());
    }

    @Test
    @DisplayName("데이터베이스 연결 확인을 위한 article 생성 테스트")
    @Transactional
    public void saveArticleTest() {
        //given
        //static article 사용

        //when
        log.info("---------------------save---------------------");
        Article saveArticle = articleRepository.save(article);
        log.info(saveArticle.toString());

        //then
        Assertions.assertNotNull(saveArticle);
        Assertions.assertEquals(saveArticle.getTitle(), article.getTitle());
    }
}
