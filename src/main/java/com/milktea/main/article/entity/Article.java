package com.milktea.main.article.entity;

import com.milktea.util.TimestampEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "article_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Article extends TimestampEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String slug;

    private String title;

    private String description;

    private String body;

    private long favoriteCount;

    @Builder
    public Article(String slug, String title, String description, String body) {
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.body = body;
        favoriteCount = 0;
    }

    //article의 모든 본문(body)을 toString에 나타낼 수 없으므로 최대 subBodyLength 길이까지만 출력하도록 함
    @Override
    public String toString() {
        int subBodyLength = 20;
        String subBody = "";

        if (body.length() < subBodyLength) subBodyLength = body.length();
        if (!Objects.isNull(body)) subBody = body.substring(0, subBodyLength);

        return String.format("id: %d, slug: %s, title: %s, description: %s, body: %s, favoriteCount: %d"
        , id, slug, title, description, subBody, favoriteCount);
    }
}
